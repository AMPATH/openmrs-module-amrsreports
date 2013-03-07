package org.openmrs.module.amrsreports.render;

import org.openmrs.Cohort;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.indicator.IndicatorResult;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.renderer.CsvReportRenderer;
import org.openmrs.module.reporting.report.renderer.RenderingException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nicholas Ingosi Magaja
 * Date: 6/13/12
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */

public class AmrReportRender extends CsvReportRenderer {
    @Override
    public void render(ReportData results, String argument, OutputStream out) throws IOException, RenderingException {
        //super.render(results, argument, out);    //To change body of overridden methods use File | Settings | File Templates.
        Writer w = new OutputStreamWriter(out,"UTF-8");
        DataSet dataset = results.getDataSets().values().iterator().next();

        List<DataSetColumn> columns = dataset.getMetaData().getColumns();

        // header row
        w.write(getBeforeRowDelimiter());
        for (DataSetColumn column : columns) {
            w.write(getBeforeColumnDelimiter());
            w.write(escape(column.getLabel()));
            w.write(getAfterColumnDelimiter());
        }
        w.write(getAfterRowDelimiter());

        // data rows
        for (DataSetRow row : dataset) {
            w.write(getBeforeRowDelimiter());
            for (DataSetColumn column : columns) {
                Object colValue = row.getColumnValue(column);
                w.write(getBeforeColumnDelimiter());
                if (colValue != null) {
                    if (colValue instanceof Cohort) {
                        w.write(escape(Integer.toString(((Cohort) colValue).size())));
                    } else if (colValue instanceof IndicatorResult) {
                        w.write(((IndicatorResult) colValue).getValue().toString());
                    }
                    else {
                        // this check is because a logic EmptyResult .toString() -> null
                        String temp = escape(colValue.toString());
                        if (temp != null)
                            w.write(temp);
                    }
                }
                w.write(getAfterColumnDelimiter());
            }
            w.write(getAfterRowDelimiter());
        }

        w.flush();
    }
}
