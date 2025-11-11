package dev.okawari.controller;

 @controller
public class UserController {
    
    @GetMapping("/signup")
    public String Signupform(){
      return "signup";  
    }

}
