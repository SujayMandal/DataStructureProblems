package com.fa.dp.rest;

import com.fa.dp.business.pmi.info.PmiInsuranceCompanyInfo;
import com.fa.dp.business.ssinvestor.delegate.SSInvestorDelegate;
import com.fa.dp.business.ssinvestor.info.SpclServicingInvestorFileInfo;
import com.fa.dp.business.ssinvestor.info.SpclServicingInvestorInfo;
import com.fa.dp.client.pojo.UploadResponse;
import com.fa.dp.rest.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class SSInvestorFileController {

	@Inject
	private SSInvestorDelegate ssInvestorDelegate;

	@RequestMapping(value = "/uploadSSInvestorFile", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<UploadResponse> uploadFile(@RequestParam(value = "file") MultipartFile file) {
		log.info("Uploading Special Servicing InvestorCode file controller begins");
		RestResponse<UploadResponse> response = new RestResponse<>();
		UploadResponse respObj = new UploadResponse();
		List<String> errorMessages = new ArrayList<>();
		String successMessage = null;

		if(!file.isEmpty()) {
			List<SpclServicingInvestorInfo> ssInvestorInfo = new ArrayList<>();
			//Validate the file and fetch all investor code and investor names from the file
			try {
				ssInvestorInfo = ssInvestorDelegate.validateFile(file);
				log.info("Uploading SS Investor file started");
				List<SpclServicingInvestorInfo> savedRecords = ssInvestorDelegate.uploadSSInvestors(file.getOriginalFilename(), ssInvestorInfo);
				successMessage = "Save Successful, " + savedRecords.size() + " records inserted";
			} catch (Exception e) {
				log.error(e.getMessage());
				errorMessages.add(e.getLocalizedMessage());
			}
		} else {
			errorMessages.add("File cannot be empty!");
		}

		if(CollectionUtils.isNotEmpty(errorMessages))
			response.setSuccess(false);
		else {
			response.setSuccess(true);
			response.setMessage(successMessage);
		}
		respObj.setErrorMessages(errorMessages);
		response.setResponse(respObj);

		log.info("Uploading Special Servicing InvestorCode file controller ends");
		return response;
	}

	@RequestMapping(value = "/getSSInvestorFiles", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<SpclServicingInvestorFileInfo>> getAllSSInvestorFiles() {
		log.info("Started getAllSSInvestorFiles() controller");
		RestResponse<List<SpclServicingInvestorFileInfo>> response = new RestResponse<>();
		try {
			List<SpclServicingInvestorFileInfo> ssInvFiles = ssInvestorDelegate.getSsInvestorFiles();
			if(CollectionUtils.isNotEmpty(ssInvFiles)) {
				response.setResponse(ssInvFiles);
			} else {
				response.setMessage("No records found");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			response.setSuccess(false);
			response.setMessage("Error occurred while fetching the records");
		}
		log.info("Ended getAllSSInvestorFiles() controller");
		return response;
	}

	@RequestMapping(value = "/uploadPMICompanies", method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<UploadResponse> uploadPMICompanies(@RequestParam(value = "file") MultipartFile file) {
		log.info("Uploading PMI Companies file controller begins");
		RestResponse<UploadResponse> response = new RestResponse<>();
		UploadResponse respObj = new UploadResponse();
		List<String> errorMessages = new ArrayList<>();
		String successMessage = null;
		if(!file.isEmpty()) {
			List<PmiInsuranceCompanyInfo> pmiCompaniesInfo = new ArrayList<>();
			//Validate the file and fetch all investor code and investor names from the file
			try {
				pmiCompaniesInfo = ssInvestorDelegate.validatePmiCompaniesFile(file);
				log.info("Uploading SS Investor file started");
				List<PmiInsuranceCompanyInfo> savedRecords = ssInvestorDelegate.uploadPmiCompanies(file.getOriginalFilename(), pmiCompaniesInfo);
				successMessage = "Save Successful, " + savedRecords.size() + " records inserted";
			} catch (Exception e) {
				log.error(e.getMessage());
				errorMessages.add(e.getLocalizedMessage());
			}
		} else {
			errorMessages.add("File cannot be empty!");
		}
		if(CollectionUtils.isNotEmpty(errorMessages))
			response.setSuccess(false);
		else {
			response.setSuccess(true);
			response.setMessage(successMessage);
		}
		respObj.setErrorMessages(errorMessages);
		response.setResponse(respObj);
		log.info("Uploading Special Servicing InvestorCode file controller ends");
		return response;

	}

}
