package com.ahasan.rest.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ahasan.rest.common.exceptions.CustomDataIntegrityViolationException;
import com.ahasan.rest.common.exceptions.RecordNotFoundException;
import com.ahasan.rest.common.messages.BaseResponse;
import com.ahasan.rest.common.messages.CustomMessage;
import com.ahasan.rest.common.utils.AppUtils;
import com.ahasan.rest.common.utils.CustomJasperReport;
import com.ahasan.rest.common.utils.JasperReportExportFormat;
import com.ahasan.rest.common.utils.JasperReportService;
import com.ahasan.rest.common.utils.Topic;
import com.ahasan.rest.dto.EmployeeDTO;
import com.ahasan.rest.dto.JasperReportDto;
import com.ahasan.rest.entity.EmployeeEntity;
import com.ahasan.rest.repo.EmployeeRepo;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;

@Service
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepo employeeRepo;
	
	
	@Autowired
	private JasperReportService jasperReportService;
	
	@Autowired
	private AppUtils appUtils;

	public List<EmployeeDTO> findEmployeeList() {
		return employeeRepo.findAll().stream().map(this::copyEmployeeEntityToDto).collect(Collectors.toList());
	}

	public EmployeeDTO findByEmployeeId(Long employeeId) {
		if (employeeRepo.existsById(employeeId)) {
			EmployeeEntity employeeEntity = employeeRepo.findByEmployeeId(employeeId);
			return copyEmployeeEntityToDto(employeeEntity);
		} else {
			throw new RecordNotFoundException(CustomMessage.DOESNOT_EXIT + employeeId);
		}
	}

	public BaseResponse createOrUpdateEmployee(EmployeeDTO employeeDTO) {
		try {
			EmployeeEntity employeeEntity = copyEmployeeDtoToEntity(employeeDTO);
			employeeRepo.save(employeeEntity);
		} catch (DataIntegrityViolationException ex) {
			throw new CustomDataIntegrityViolationException(ex.getCause().getCause().getMessage());
		}
		return new BaseResponse(Topic.EMPLOYEE.getName() + CustomMessage.SAVE_SUCCESS_MESSAGE);
	}

	public BaseResponse deleteEmployeeById(Long employeeId) {
		if (employeeRepo.existsById(employeeId)) {
			employeeRepo.deleteById(employeeId);
		} else {
			throw new RecordNotFoundException(CustomMessage.NO_RECOURD_FOUND + employeeId);
		}
		return new BaseResponse(Topic.EMPLOYEE.getName() + CustomMessage.DELETE_SUCCESS_MESSAGE);

	}

	private EmployeeDTO copyEmployeeEntityToDto(EmployeeEntity employeeEntity) {
		EmployeeDTO employeeDTO = new EmployeeDTO();
		BeanUtils.copyProperties(employeeEntity, employeeDTO);
		return employeeDTO;
	}

	private EmployeeEntity copyEmployeeDtoToEntity(EmployeeDTO employeeDTO) {
		EmployeeEntity employeeEntity = new EmployeeEntity();
		BeanUtils.copyProperties(employeeDTO, employeeEntity);
		return employeeEntity;
	}

	public CustomJasperReport generateUserReport(String searchTerm) {
		Sort sort = Sort.by("auEntryAt").descending();
		List<EmployeeDTO> listOfUserAccesse = findEmployeeList();
		CustomJasperReport report = new CustomJasperReport();
        report.setOutputFilename("Employee Info Report");
        report.setReportName("users");
        report.setReportDir("/report/empl");
        return generaterJasperReportFinalization(listOfUserAccesse, report);
	}
	
	public CustomJasperReport generaterJasperReportFinalization(List<?> listOfResponeEntityList, CustomJasperReport customJasperReportDto) {
		List<JasperReportDto> listOfJasperReportDto = new ArrayList<>();
		JasperReportDto jasperReportDto = new JasperReportDto();
        jasperReportDto.setResponeEntityList(listOfResponeEntityList);
        listOfJasperReportDto.add(jasperReportDto);
        
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        CustomJasperReport report = new CustomJasperReport();
        
        report.setOutputFilename(customJasperReportDto.getOutputFilename() + " Report");
        report.setReportName(customJasperReportDto.getReportName());
        report.setReportDir(getResoucePath(customJasperReportDto.getReportDir()) + "/");
        report.setReportFormat(printFormat("PDF"));
        report.setParameters(parameterMap);
        report.setReportData(listOfJasperReportDto);

        ByteArrayOutputStream baos = null;

        try {
            baos = jasperReportService.generateReport(report);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	appUtils.finallyOutputStream(baos);
        }

        report.setContent(baos.toByteArray());

        return report;
	}
	
	
	public String getResoucePath(String filePath) {
		Resource resource = new ClassPathResource(filePath);
		try {
			return resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public JasperReportExportFormat printFormat(String printFormat) {

		if (printFormat == null) {
			return JasperReportExportFormat.PDF_FORMAT;
		}

		if (printFormat.startsWith("PDF")) {
			return JasperReportExportFormat.PDF_FORMAT;
		} else if (printFormat.startsWith("HTML")) {
			return JasperReportExportFormat.HTML_FORMAT;
		} else if (printFormat.startsWith("DOCX")) {
			return JasperReportExportFormat.DOCX_FORMAT;
		}

		if (printFormat.startsWith("XLSX")) {
			return JasperReportExportFormat.XLSX_FORMAT;
		}

		return JasperReportExportFormat.PDF_FORMAT;
	}
	
	public void finallyOutputStream(ByteArrayOutputStream baos) {

		if (baos != null) {
			try {
				baos.close();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}

}
