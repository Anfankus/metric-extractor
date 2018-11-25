package cn.cp;

import cn.cp.controller.MetricsExtractor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.labels.*;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.StandardChartTheme;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.io.InvalidObjectException;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class UI extends JFrame{
    private JTextArea textArea1;
    private JButton 选择文件Button;
    private JButton 清除内容Button;
    private JButton 开始计算Button;
    private JPanel panel2;
    private JPanel panel1;
    private JPanel panel;
    private JTable table;
    private JComboBox comboBox1;
    private JButton button1;
    private JTable table1;
    private JButton 测试数据Button;
    private JTextField textField1;
    private JButton 查询Button;
    private ArrayList<String> FilePath;
    private List<String> FileName;
    private ArrayList version;
    private HashMap<String,ArrayList<Double>> data;
    private int length;
    private String classname;

    public UI(){
      panel1=new JPanel();
      panel2=new JPanel();
      textArea1.setEnabled(false);
      addData();
      JFrame frame = new JFrame("变更预测");
      frame.setSize(400,400);
      frame.setContentPane(this.panel);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLocationRelativeTo(null);
      frame.pack();
      frame.setVisible(true);
      FilePath=new ArrayList<String>();
      FileName=new ArrayList();
      data=new HashMap<String,ArrayList<Double>>();
      version=new ArrayList();

      选择文件Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              FilePath.add(chooseFile());
          }
        });
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classname=textField1.getText();
                LineChart l=new LineChart(data,classname,length);
            }
        });

        清除内容Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBox1.removeAllItems();
                textArea1.setText("");
                FilePath.clear();
                FileName.clear();
            }
        });
        开始计算Button.addActionListener(e -> {
            String[] paths = (String[]) FilePath.toArray(new String[0]);
            MetricsExtractor m=new MetricsExtractor(paths);
            try {
                m.doExtract(s->{

                    length=s.getMetrics().size();
                  data=s.getChangeRate();
                },true);
            } catch (InvalidObjectException e1) {
                e1.printStackTrace();
            }
        });
        测试数据Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MetricsTable m=new MetricsTable(data,length);
            }
        });
        查询Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                classname=textField1.getText();
            }
        });

        comboBox1.addItemListener(new ItemListener()
          {
             public void itemStateChanged(ItemEvent event) {
                 textField1.setText(event.getItem().toString());

             } });

    }
    //选择文件
    public String chooseFile(){
        JFrame chatFrame=new JFrame();
        int result = 0;
        String path = null;
        JFileChooser fileChooser = new JFileChooser();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        System.out.println(fsv.getHomeDirectory());
        fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
        fileChooser.setDialogTitle("请选择要上传的文件...");
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        result = fileChooser.showOpenDialog(chatFrame);
        if (JFileChooser.APPROVE_OPTION == result) {
            path=fileChooser.getSelectedFile().getPath();
            System.out.println("path: "+path);
        }
        String fName=path.trim();
        String fileName=fName.substring(fName.lastIndexOf("\\")+1);
        FileName.add(fileName);
        comboBox1.addItem(fileName);
        System.out.println("fileName = "+fileName);
        textArea1.append(path+"\r\n");
        return path;

    }

    //向表格中添加数据
    public void addData(){
        Vector vData = new Vector();
        Vector vName = new Vector();
        vName.add("column1");
        vName.add("column2");
        Vector vRow = new Vector();
        vRow.add("cell 0 0");
        vRow.add("cell 0 1");

        vData.add(vRow.clone());
        vData.add(vRow.clone());
        String[] Names = { "姓名", "语文", "数学", "总分", "及格","Java" };
        String[] col={"Metrics","Class"};
        DefaultTableModel model = new DefaultTableModel(col,0);
        model.addRow(col);
        model.addRow(col);
        table1.setModel(model);

    }

    public void getVersion(ArrayList version){
        for(int i=0;i<version.size();i++)
            comboBox1.addItem(version.get(i));
    }

    public static void main(String[] args) {
           UI u=new UI();

    }
}

class LineChart{
    public LineChart(HashMap<String,ArrayList<Double>> d,String name,int length2){
        // 步骤1：创建CategoryDataset对象（准备数据）
        String[] rowKeys = {name};
        String[] colKeys = new String[length2];
        for(int i=0;i<length2;i++)
            colKeys[i]=String.valueOf(i);
//        String[] colKeys = {"0:00", "1:00", "2:00", "7:00", "8:00", "9:00",
//                "10:00", "11:00", "12:00", "13:00", "16:00", "20:00", "21:00",
//                "23:00"};
        double[][] data =new double[1][length2];
        for (String key : d.keySet()){
            if(name.equals(d.keySet())){
                for(int i=0;i<length2;i++)
                    data[0][i]=d.get(key).get(i);
                break;
            }

        }
       // double[][] data = {{4, 3, 1, 1, 1, 1, 2, 2, 2, 1, 8, 2, 1, 1},};
        ArrayList a=new ArrayList();
        a.add(rowKeys);
        a.add(colKeys);
        a.add(data);
        CategoryDataset dataset = createDataset(a);
        // 步骤2：根据Dataset 生成JFreeChart对象，以及做相应的设置
        JFreeChart freeChart = createChart(dataset);
        setChineseTheme(freeChart);
        ChartFrame chartFrame=new ChartFrame("某公司人员组织数据图",freeChart);
        //chart要放在Java容器组件中，ChartFrame继承自java的Jframe类。该第一个参数的数据是放在窗口左上角的，不是正中间的标题。
        chartFrame.pack(); //以合适的大小展现图形
        chartFrame.setVisible(true);//图形是否可见

    }
    // 根据CategoryDataset创建JFreeChart对象
    public static JFreeChart createChart(CategoryDataset categoryDataset) {
        // 创建JFreeChart对象：ChartFactory.createLineChart
        JFreeChart jfreechart = ChartFactory.createLineChart("不同类别按小时计算拆线图", // 标题
                "年分", // categoryAxisLabel （category轴，横轴，X轴标签）
                "数量", // valueAxisLabel（value轴，纵轴，Y轴的标签）
                categoryDataset, // dataset
                PlotOrientation.VERTICAL, true, // legend
                false, // tooltips
                false); // URLs
        // 使用CategoryPlot设置各种参数。以下设置可以省略。
        CategoryPlot plot = (CategoryPlot)jfreechart.getPlot();
        // 背景色 透明度
        plot.setBackgroundAlpha(0.5f);
        // 前景色 透明度
        plot.setForegroundAlpha(0.5f);
        // 其他设置 参考 CategoryPlot类
        LineAndShapeRenderer renderer = (LineAndShapeRenderer)plot.getRenderer();
        renderer.setBaseShapesVisible(true); // series 点（即数据点）可见
        renderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
        renderer.setUseSeriesOffset(true); // 设置偏移量
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        return jfreechart;
    }
    public static void setChineseTheme(JFreeChart chart) {
        //设置支持中文的字体
        Font FONT = new Font("宋体", Font.PLAIN, 12);

        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        // 设置标题字体
        chartTheme.setExtraLargeFont(FONT);
        // 设置图例的字体
        chartTheme.setRegularFont(FONT);
        // 设置轴向的字体
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        ChartFactory.setChartTheme(chartTheme);
        // 使当前主题马上生效
        ChartUtilities.applyCurrentTheme(chart);
    }


    public static CategoryDataset createDataset(ArrayList Mdata) {
        String[] rowKey= (String[]) Mdata.get(0);
        String[] colKey= (String[]) Mdata.get(1);
        double[][] data= (double[][]) Mdata.get(2);
        return DatasetUtilities.createCategoryDataset(rowKey, colKey, data);
    }
}

class MetricsTable{
    public MetricsTable(HashMap<String,ArrayList<Double>> d,int length1){
        JFrame f = new JFrame();
        Object[][] playerInfo = new Object[d.size()][length1+1];
        int a=0;
        for (String key : d.keySet()) {
            playerInfo[a][0] = key;

            for(int i=1;i<d.get(key).size()+1;i++) {
                playerInfo[a][i] = (double) Math.round(d.get(key).get(i - 1) * 10000) / 10000;
            }
            for(int i=d.get(key).size()+1;i<length1+1;i++) {
                    playerInfo[a][i] = 0;
            }
            a++;
        }

        // 创建表格中的横标题
        String[] Names = new String[length1+1];
        Names[0]="class";
        for(int i=1;i<length1+1;i++)
            Names[i]=String.valueOf(i);
        // 以Names和playerInfo为参数，创建一个表格
        JTable table = new JTable(playerInfo, Names);
        // 设置此表视图的首选大小
        table.setPreferredScrollableViewportSize(new Dimension(550, 100));
        // 将表格加入到滚动条组件中
        JScrollPane scrollPane = new JScrollPane(table);
        f.getContentPane().add(scrollPane, BorderLayout.CENTER);
        // 再将滚动条组件添加到中间容器中
        f.setTitle("表格测试窗口");
        f.pack();
        f.setVisible(true);
        f.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }
}