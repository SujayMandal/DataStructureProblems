package com.fa.dp.rest;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public ModelAndView handleError(HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView();

		if(response.getStatus() == HttpStatus.NOT_FOUND.value()) {
			modelAndView.setViewName("404.html");
		}
		else if(response.getStatus() == HttpStatus.FORBIDDEN.value()) {
			modelAndView.setViewName("403.html");
		}
		else if(response.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
			modelAndView.setViewName("500.html");
		}
		else if(response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
			modelAndView.setViewName("401.html");
		}
		else if(response.getStatus() == HttpStatus.METHOD_NOT_ALLOWED.value()) {
			modelAndView.setViewName("401.html");
		}
		else {
			modelAndView.setViewName("error.html");
		}

		return modelAndView;
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
