package Functions;

import java.io.File;
import java.io.FileOutputStream;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class WriteData {
    public static void initiSheet1(WritableWorkbook workbook, WritableSheet sheet) throws Exception {
        // File currDir = new File(".");
        // String path = currDir.getAbsolutePath();
        // String fileLocation = path.substring(0, path.length() - 1) + "dataset.xls";

        // WritableWorkbook workbook = Workbook.createWorkbook(new File(fileLocation));

        // WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

        WritableCellFormat headerFormat = new WritableCellFormat();
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        headerFormat.setFont(font);

        Label headerLabel = new Label(0, 0, "Simulation id", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(1, 0, "Timestamp", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(2, 0, "Service Name", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(3, 0, "Number of VMs used", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(4, 0, "Type of VM", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(5, 0, "Number of VNF", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(6, 0, "Type of VNFs", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(7, 0, "Allocation Policy", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(8, 0, "Request Rate(λ)", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(9, 0, "Size of request", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(10, 0, "Service duration(h)", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(11, 0, "Service cost(€)", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(12, 0, "Service Energy cost(€)", headerFormat);
        sheet.addCell(headerLabel);

    }

    public static void initSheet2(WritableWorkbook workbook, WritableSheet sheet) throws Exception {
        WritableCellFormat headerFormat = new WritableCellFormat();
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        headerFormat.setFont(font);

        Label headerLabel = new Label(0, 0, "Simulation id", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(1, 0, "Timestamp", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(2, 0, "Number of Servers", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(3, 0, "Total Servers'RAM", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(4, 0, "Total Servers'CPU", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(5, 0, "Total Servers'Storage", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(6, 0, "Total Servers'Network interfaces", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(7, 0, "Number of VMs active", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(8, 0, "Type of VM", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(9, 0, "Number of Services running", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(10, 0, "Services running", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(11, 0, "Allocation Policy", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(12, 0, "Request Rate(λ)", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(13, 0, "Total RAM used", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(14, 0, "Total CPU used", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(15, 0, "Total Storage used", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(16, 0, "Energy cost(€)", headerFormat);
        sheet.addCell(headerLabel);

        headerLabel = new Label(17, 0, "Availability of renewable energy", headerFormat);
        sheet.addCell(headerLabel);
    }

    public static void insertStringCell(WritableSheet sheet, int rownumber, int colnumber, String field)
            throws Exception {
        WritableCellFormat cellFormat = new WritableCellFormat();
        cellFormat.setWrap(true);
        Label cellLabel = new Label(colnumber, rownumber, field, cellFormat);
        sheet.addCell(cellLabel);
    }

    public static void insertIntCell(WritableSheet sheet, int rownumber, int colnumber, int field) throws Exception {
        WritableCellFormat cellFormat = new WritableCellFormat();
        cellFormat.setWrap(true);
        Number cellLabel = new Number(colnumber, rownumber, field, cellFormat);
        sheet.addCell(cellLabel);
    }

    public static void insertDoubleCell(WritableSheet sheet, int rownumber, int colnumber, double field)
            throws Exception {
        WritableCellFormat cellFormat = new WritableCellFormat();
        cellFormat.setWrap(true);
        Number cellLabel = new Number(colnumber, rownumber, field, cellFormat);
        sheet.addCell(cellLabel);
    }

}
