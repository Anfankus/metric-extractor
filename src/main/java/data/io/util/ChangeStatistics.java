package data.io.util;

import static java.lang.System.out;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeStatistics {
	/*private String outputPathOfCSV;

	public ChangeStatistics(String outputPathOfCSV) {
		this.outputPathOfCSV = outputPathOfCSV;
		Helper.isNotExitedThenCreatPath(outputPathOfCSV);
	}

	private boolean isValidPath(String csvFilePath) {
		boolean flag = true;
		File file = new File(csvFilePath);
		if (file.isDirectory() != true) {
			flag = false;
		}
		return flag;
	}


	public void printFineGrainedCodeChange(List<SourceCodeChange> listOfFineGrainedCodeChange) {
		out.println("\n" + "-FineGrainedCodeChange" + "\n");

		if (isValidPath(outputPathOfCSV) != true) {
			System.err.println("InvalidPath: " + outputPathOfCSV);
			System.err.println("ErrorLocation: " + "DiffAnalyzer.printPacakgeChange()" + "\n");
			return;
		}

		for (SourceCodeChange change : listOfFineGrainedCodeChange) {
			out.println("RootEntity: " + change.getRootEntity());
			out.println("ChangeType: " + change.getChangeType());
			out.println("ParentEntity: " + change.getParentEntity());
			out.println("CodeEntity: " + change.getChangedEntity());
			out.println();
		}
		String outputAddress = outputPathOfCSV + "/FineGrainedCodeChange.csv";
		DataWriter writer = new DataWriter(outputAddress);
		String[] head = { "RootEntity", "Change Type", "Change Details" };
		writer.writeArrayToCSVFile(head);

		//Set<String> setOfClassName1 = mapOfClassComplexity1.keySet();
		//Set<String> setOfClassName2 = mapOfClassComplexity2.keySet();

		for (SourceCodeChange change : listOfFineGrainedCodeChange) {
			String changeType = change.getChangeType().toString();
			if (changeType.equals("DOC_DELETE") || changeType.equals("DOC_INSERT")
					|| changeType.equals("DOC_UPDATE") || changeType.equals("COMMENT_DELETE")
					|| changeType.equals("COMMENT_INSERT") || changeType.equals("COMMENT_MOVE")
					|| changeType.equals("COMMENT_UPDATE")) {
			} else {
				String fullyClassName = change.getRootEntity().toString();
				// double cc1 = mapOfClassComplexity1.get(fullyClassName);
				// double cc2 = mapOfClassComplexity2.get(fullyClassName);
				String changeEntity = change.getChangedEntity().toString();
				String[] info = { fullyClassName, changeType, changeEntity };
				writer.writeArrayToCSVFile(info);
			}
		}
		writer.close();
	}

	public void printClassLevelComplexity(List<SourceCodeChange> listOfFineGrainedCodeChange) {
		String outputAddress = outputPathOfCSV + "/ClassLevelComplexityEvolution.csv";
		DataWriter writer = new DataWriter(outputAddress);
		String[] head = { "RootEntity", "Change Type", "Change Details" };
		writer.writeArrayToCSVFile(head);

		//Set<String> setOfClassName1 = mapOfClassComplexity1.keySet();
		//Set<String> setOfClassName2 = mapOfClassComplexity2.keySet();

		for (SourceCodeChange change : listOfFineGrainedCodeChange) {
			String changeType = change.getChangeType().toString();
			if (changeType.equals("DOC_DELETE") || changeType.equals("DOC_INSERT")
					|| changeType.equals("DOC_UPDATE") || changeType.equals("COMMENT_DELETE")
					|| changeType.equals("COMMENT_INSERT") || changeType.equals("COMMENT_MOVE")
					|| changeType.equals("COMMENT_UPDATE")) {
				continue;
			}

			String fullyClassName = change.getRootEntity().toString();
			// double cc1 = mapOfClassComplexity1.get(fullyClassName);
			// double cc2 = mapOfClassComplexity2.get(fullyClassName);
			String changeEntity = change.getChangedEntity().toString();
			String[] info = { fullyClassName, changeType, changeEntity };
			writer.writeArrayToCSVFile(info);
		}
		writer.close();

	}

	public void printOrderedFineGrainedCodeChange() {
		class Item {
			String changeType;
			String changeEntity;

			Item(String changeType, String changeEntity) {
				this.changeType = changeType;
				this.changeEntity = changeEntity;
			}
		}

		String intputAddress = "C:\\Desktop\\result\\ClassLevelComplexityEvolution.csv";
		String outputAddress = "C:\\Desktop\\result\\ClassLevelComplexityEvolution2.csv";
		DataReader reader = new DataReader(intputAddress);
		List<String[]> listOfValueWithoutHeader = reader.readRecordWithoutHeaderToList();

		Map<String, ArrayList<Item>> classChangeMap = new HashMap<String, ArrayList<Item>>();

		for (String[] element : listOfValueWithoutHeader) {
			//size=3, item[0]=fullClassName, item[1]=changeType, item[2]=changeEntity
			String fullClassName = element[0];
			String changeType = element[1];
			String changeEntity = element[2];
			Item item = new Item(changeType, changeEntity);
			if (classChangeMap.containsKey(fullClassName)) {
				ArrayList<Item> newList = classChangeMap.get(fullClassName);
				newList.add(item);
				classChangeMap.replace(fullClassName, newList);
			} else {
				ArrayList<Item> newList = new ArrayList<Item>();
				newList.add(item);
				classChangeMap.put(fullClassName, newList);
			}
		}

		DataWriter writer = new DataWriter(outputAddress);
		Set<String> keys = classChangeMap.keySet();
		for (String className : keys) {
			List<Item> values = classChangeMap.get(className);
			String allChangeTypes = "";
			String allChangeEntities = "";
			for (Item item : values) {
				allChangeTypes = allChangeTypes + item.changeType + "\n";
				allChangeEntities = allChangeEntities + item.changeEntity + "\n";
			}

			String[] arr = { className, allChangeTypes, allChangeEntities };
			writer.writeArrayToCSVFile(arr);
		}
		writer.close();
	}

	public void printFieldChange(List<RemoveField> listOfRemovedField, List<AddField> listOfAddedField) {
		if (listOfRemovedField.size() > 0 || listOfAddedField.size() > 0) {
			out.println("\n" + "-FieldLevelChange" + "\n");
		}
		String outputAddress = outputPathOfCSV;
		outputAddress = outputPathOfCSV + "/FieldLevelChange.csv";
		DataWriter writer = new DataWriter(outputAddress);
		String[] head = { "changeType", "changeEntityName", "projectName", "versionNo", "packageName",
				"compilationUnitName", "className" };
		writer.writeArrayToCSVFile(head);
		for (RemoveField field : listOfRemovedField) {
			writer.writeArrayToCSVFile(field.getResult());
			out.println(field.toString2());
		}
		for (AddField field : listOfAddedField) {
			writer.writeArrayToCSVFile(field.getResult());
			out.println(field.toString2());
		}
		writer.close();
	}

	public void printMethodChange(List<AddMethod> listOfAddedMethod, List<RemoveMethod> listOfRemovedMethod) {
		if (listOfRemovedMethod.size() > 0 || listOfAddedMethod.size() > 0) {
			out.println("\n" + "-MethodLevelChange" + "\n");
		}
		String outputAddress = outputPathOfCSV;
		outputAddress = outputPathOfCSV + "/MethodLevelChange.csv";
		DataWriter writer = new DataWriter(outputAddress);

		String[] head = { "changeType", "changeEntityName", "projectName", "versionNum", "packageName",
				"compilationUnitName", "fullClassName" };
		writer.writeArrayToCSVFile(head);
		for (RemoveMethod rm : listOfRemovedMethod) {
			writer.writeArrayToCSVFile(rm.getResult());
			out.println(rm.toString());
		}
		for (AddMethod am : listOfAddedMethod) {
			writer.writeArrayToCSVFile(am.getResult());
			out.println(am.toString());
		}
		writer.close();
	}

	public void outputToFile(int[][] loc2cc) {
		//loc2cc[i][j]
		int firstDim = loc2cc.length;
		int secondDim = 0;
		double[][] result = new double[3][3];
		if (firstDim == 0) {
			System.err.println("2-dim arry, first dim = 0");
		} else {
			secondDim = loc2cc[0].length;
			double[] rowSum = new double[3];
			for (int j = 0; j < secondDim; j++) {
				rowSum[0] = rowSum[0] + loc2cc[0][j];
				rowSum[1] = rowSum[1] + loc2cc[1][j];
				rowSum[2] = rowSum[2] + loc2cc[2][j];
			}
			DecimalFormat df = new DecimalFormat("0.####");
			DataWriter writer = new DataWriter(this.outputPathOfCSV);
			for (int i = 0; i < firstDim; i++) {
				for (int j = 0; j < secondDim; j++) {
					result[i][j] = loc2cc[i][j]/rowSum[i];
				}
				String[] info = {df.format(result[i][0]),df.format(result[i][1]),df.format(result[i][2])};
			    writer.writeArrayToCSVFile(info);
			}
			writer.close();
		}
	}*/
}
