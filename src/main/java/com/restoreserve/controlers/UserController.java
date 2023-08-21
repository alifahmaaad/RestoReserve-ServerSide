package com.restoreserve.controlers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.RegisterUserDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateUserDto;
import com.restoreserve.model.entities.User;
import com.restoreserve.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<ResponseData<User>> register(@Valid @RequestBody RegisterUserDto userDto,Errors errs){
        ResponseData<User> dataResponse = new ResponseData<>(false, null, null);
        if(errs.hasErrors()){
            for (ObjectError err : errs.getAllErrors()) {
            dataResponse.getMessage().add(err.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(dataResponse);
        }
        User dataUser = modelMapper.map(userDto, User.class);
        try {
            if(userService.isUserExistsWithUsernameOrEmail(userDto.getUsername(), userDto.getEmail())){
                dataResponse.getMessage().add("Username or Email already taken");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            dataResponse.setPayload(userService.create(dataUser));
            dataResponse.setStatus(true);
            dataResponse.getMessage().add("Your Account has been succesfully created or registered");
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<User>> getUserById(@PathVariable Long id){
        ResponseData<User> dataResponse=new ResponseData<>(false, null, null);
        try {
            boolean isExists = userService.isUserExists(id);
            if(isExists){
                dataResponse.setPayload(userService.getUserById(id));
                dataResponse.getMessage().add("Success get data user with id :"+id);
                dataResponse.setStatus(true);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Failed: Data user with id :"+id+" Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
             dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    // "/appadmin" for role app admin and super admin only
    @GetMapping("/appadmin")
    public ResponseEntity<ResponseData<List<User>>> getAllUser(){
        ResponseData<List<User>> dataResponse=new ResponseData<>(false, null, null);
        try {
            dataResponse.setPayload(userService.getAllUser());
            dataResponse.getMessage().add("Success get All data user");
            dataResponse.setStatus(true);
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<ResponseData<User>> updateUser(@RequestBody UpdateUserDto userDto){
        ResponseData<User> dataResponse = new ResponseData<>(false, null, null);
        try {
            boolean isExists=userService.isUserExists(userDto.getId());
            if(isExists){
                User user = modelMapper.map(userDto, User.class);
                dataResponse.setPayload(userService.update(user));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success Update user with id: "+user.getId());
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("User Not Exists id: "+userDto.getId());
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    // "/appadmin" for role app admin and super admin only
    @DeleteMapping("/appadmin/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){
        ResponseData<?> dataResponse =new ResponseData<>(false, null, null);
        try {
            boolean isExists = userService.isUserExists(id);
            if(isExists){
                userService.deleteById(id);
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("User Deleted With id:"+id);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Failed: User Not Found, id:"+id);
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
}