package com.ca.umg.rt.web.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
/**
 * Controller for UMG runtime admin user interface.
 * 
 * @author devasiaa
 *
 */
//@Controller
public class HomeController {
    @RequestMapping(value = { "/", "/index" })
    public String renderHome() {
        return "hello";
    }
    
    @RequestMapping(value = { "/landing" })
    public String renderLanding() {
        return "index";
    }

    @RequestMapping("/login")
    public String renderLogin() {
        return "login";
    }

    @RequestMapping("/register")
    public String renderRegister() {
        return "register";
    }
}
