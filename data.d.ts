declare namespace Metric {
  let data: {
    //输入的各个版本度量计算结果
    versionInfo: Array<{
      projectName: string,
      version: string,
      //基本信息，键值对格式
      basic: Array<{
        key: string,
        value: number
      }>,
      //度量信息
      metric: {
        //度量值表头属性
        head: Array<string>//例如['className','type','changeValue,'loc'],
        //度量值表数据
        data: Array<Array<string>>
        //例如[
        //  ['org.junit.junit','class','65,'16'],
        //  ['org.junit.junit','interface','0','16']
        // ]
      },
    }>,
    //预测相关
    predict: {
      version: string,
      //预测结果，对应版本的每一个类一个是否变更的值
      data: Array<{
        className: string,
        isChanged: boolean
      }>,
      //预测使用的学习器相关信息，键值对格式，如 {key:'recall',value:'0.9'}
      modelInfo: Array<{
        key: string,
        value: string
      }>
    }
  };
}