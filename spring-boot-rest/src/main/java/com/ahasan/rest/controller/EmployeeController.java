package com.ahasan.rest.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ahasan.rest.common.messages.BaseResponse;
import com.ahasan.rest.common.messages.CustomMessage;
import com.ahasan.rest.common.utils.CustomJasperReport;
import com.ahasan.rest.common.utils.Topic;
import com.ahasan.rest.dto.EmployeeDTO;
import com.ahasan.rest.service.EmployeeService;

//@Validated
@RestController
@RequestMapping("/employee")
public class EmployeeController extends ReportBaseController{

	@Autowired
	private EmployeeService employeeService;

	@GetMapping(value = "/find")
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
		List<EmployeeDTO> list = employeeService.findEmployeeList();
		return new ResponseEntity<List<EmployeeDTO>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/find/by-id")
	public ResponseEntity<EmployeeDTO> getEmployeeById(@NotNull(message = "Id can't be null") @RequestParam Long id) {
		EmployeeDTO list = employeeService.findByEmployeeId(id);
		return new ResponseEntity<EmployeeDTO>(list, HttpStatus.OK);
	}

	@PostMapping(value = { "/add", "/update" })
	public ResponseEntity<BaseResponse> createOrUpdateEmployee( @RequestBody EmployeeDTO employeeDTO) {
		BaseResponse response = employeeService.createOrUpdateEmployee(employeeDTO);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<BaseResponse> deleteEmployeeById(@PathVariable("id") Long id) {
		BaseResponse response = employeeService.deleteEmployeeById(id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	@PostMapping("/generate-users-report")
    ResponseEntity<byte[]> generateUserReport(@RequestParam(required = false) String searchTerm){
        try {
            CustomJasperReport report = employeeService.generateUserReport(searchTerm);
            return respondReportOutputWithoutHeader(report, false);
        } catch (Exception e) {
            throw new RuntimeException(Topic.EMPLOYEE.getName() + CustomMessage.SAVE_SUCCESS_MESSAGE);
        }
    }
}
