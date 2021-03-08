package com.ahasan.rest.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ahasan.rest.dto.JasperReportDto;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;

@Service
public class JasperReportService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportService.class);
	
	
	 @Autowired
	 private AppUtils appUtils;

    /**
     * Return the suitable Exporter for a given file format.
     *
     * @param format
     * @return exporter
     * @throws Exception
     */
    private static ExporterOutput getExporterOutput(JasperReportExportFormat format, ByteArrayOutputStream byteArrayOutputStream) throws Exception {
        switch (format) {
            // common cases
            case PDF_FORMAT:
            case XLS_FORMAT:
            case XLSX_FORMAT:
            case PPTX_FORMAT:
            case DOCX_FORMAT:
            case ODT_FORMAT:
            case ODS_FORMAT:
                return new SimpleOutputStreamExporterOutput(byteArrayOutputStream);

            // specific cases
            case HTML_FORMAT:
                return new SimpleHtmlExporterOutput(byteArrayOutputStream);
            case CSV_FORMAT:
                return new SimpleWriterExporterOutput(byteArrayOutputStream);
            case RTF_FORMAT:
                return new SimpleWriterExporterOutput(byteArrayOutputStream);
            default:
                throw new Exception("Invalid format");
        }
    }

    /**
     * Generate a exporter with for a CusJasperReportDef. Note that SUBREPORT_DIR an
     * locale have default values.
     *
     * @param reportDef
     * @return Exporter
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private static Exporter generateExporter(CustomJasperReport reportDef) throws Exception {
        /*
         * if (reportDef.getParameters().SUBREPORT_DIR == null) {
         * reportDef.parameters.SUBREPORT_DIR = reportDef.getReportDir(); }
         *
         * if (reportDef.locale) { reportDef.parameters.REPORT_LOCALE = reportDef.locale
         * } else { reportDef.parameters.REPORT_LOCALE = Locale.getDefault() }
         */

        Exporter exporter = getExporter(reportDef.getReportFormat());

        /*
         * if (reportDef.getUseDefaultConfiguration()) {
         * applyDefaultConfiguration(exporter, reportDef); } if
         * (!reportDef.getReportConfiguration().isEmpty()) { Field[] fields =
         * getExporterFields(reportDef.getReportFormat());
         * applyCustomConfiguration(fields, exporter,
         * reportDef.getReportConfiguration()); }
         */
        return exporter;
    }

    @SuppressWarnings({"rawtypes", "unused"})
    private static void applyCustomConfiguration(Field[] fields, Exporter exporter, Map<String, Object> configuration) {
        // TODO: based on format apply appropriate user defined configuration
        /*
         * def fieldNames = fields.collect { it.getName() }
         *
         * configuration.each { p -> if (fieldNames.contains(p.getKey())) { def fld =
         * Class.forName(fields.find { it.name = p.getKey()
         * }.clazz.name).getField(p.getKey())
         * exporter.setParameter(fld.get(fld.root.class), p.getValue()) } }
         */
    }

    @SuppressWarnings({"rawtypes", "unused"})
    private static void applyDefaultConfiguration(Exporter exporter, CustomJasperReport format) {
        // TODO: based on format apply common configuration
    }

    /**
     * Return the suitable Exporter for a given file format.
     *
     * @param format
     * @return exporter
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private static Exporter getExporter(JasperReportExportFormat format) throws Exception {
        switch (format) {
            case PDF_FORMAT:
                return new JRPdfExporter();
            case HTML_FORMAT:
                return new HtmlExporter();
            case CSV_FORMAT:
                return new JRCsvExporter();
            case XLS_FORMAT:
                return new JRXlsExporter();
            case RTF_FORMAT:
                return new JRRtfExporter();
            case ODT_FORMAT:
                return new JROdtExporter();
            case ODS_FORMAT:
                return new JROdsExporter();
            case DOCX_FORMAT:
                return new JRDocxExporter();
            case XLSX_FORMAT:
                return new JRXlsxExporter();
            case PPTX_FORMAT:
                return new JRPptxExporter();
            default:
                throw new Exception("Invalid format");
        }
    }

    public ByteArrayOutputStream generateReport(CustomJasperReport reportDef) throws Exception {

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        Exporter exporter = generateExporter(reportDef);
        // exporter output
        exporter.setExporterOutput(getExporterOutput(reportDef.getReportFormat(), byteArray));
        // exporter input
        JasperPrint jasperPrint = generatePrinter(reportDef);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        // export
        exporter.exportReport();

        return byteArray;
    }

    /**
     * Generate a JasperPrint object for a given report.
     *
     * @param reportDefinition , the report
     * @param parameters       , additional parameters
     * @return JasperPrint , jasperreport printer
     * @throws IOException
     * @throws JRException
     */
    private JasperPrint generatePrinter(CustomJasperReport reportDef) throws JRException, IOException {
        JasperPrint jasperPrint = null;
        
        Resource resource = reportDef.getReport();
        
        JRDataSource jrDataSource = reportDef.getDataSource();

        if (jrDataSource == null && reportDef.getReportData() != null && !reportDef.getReportData().isEmpty()) {
            jrDataSource = new JRBeanCollectionDataSource(reportDef.getReportData());
        }

        if (jrDataSource != null) {
            if (resource.getFilename().endsWith(".jasper")) {
                jasperPrint = JasperFillManager.fillReport(resource.getInputStream(), reportDef.getParameters(), jrDataSource);

            } else {
                jasperPrint = JasperFillManager.fillReport(
                        JasperCompileManager.compileReport(resource.getInputStream()), reportDef.getParameters(),
                        jrDataSource);
            }
        } else {
            if (resource.getFilename().endsWith(".jasper")) {
                jasperPrint = JasperFillManager.fillReport(resource.getInputStream(), reportDef.getParameters(),
                        jrDataSource);
            } else {
                jasperPrint = JasperFillManager.fillReport(
                        JasperCompileManager.compileReport(resource.getInputStream()), reportDef.getParameters(),
                        jrDataSource);
            }

        }

        return jasperPrint;
    }
    
    public CustomJasperReport generaterJasperReportFinalization(List<?> listOfResponeEntityList, CustomJasperReport customJasperReportDto) {
		List<JasperReportDto> listOfJasperReportDto = new ArrayList<>();
		JasperReportDto jasperReportDto = new JasperReportDto();
        jasperReportDto.setHospitalName( "Border Guard Hospital");
        jasperReportDto.setHospitalAddress("Dhaka, Bangladesh");
        jasperReportDto.setOthersInformation("Telemedicine, Call: 9999");
        jasperReportDto.setResponeEntityList(listOfResponeEntityList);
        listOfJasperReportDto.add(jasperReportDto);
        
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        CustomJasperReport report = new CustomJasperReport();
        
        report.setOutputFilename(customJasperReportDto.getOutputFilename() + " Report");
        report.setReportName(customJasperReportDto.getReportName());
        report.setReportDir(appUtils.getResoucePath(customJasperReportDto.getReportDir()) + "/");
        report.setReportFormat(appUtils.printFormat("PDF"));
        report.setParameters(parameterMap);
        report.setReportData(listOfJasperReportDto);

        ByteArrayOutputStream baos = null;

        try {
            baos = generateReport(report);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	appUtils.finallyOutputStream(baos);
        }

        report.setContent(baos.toByteArray());

        return report;
	}

}
