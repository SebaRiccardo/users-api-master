package unsl.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import unsl.entities.Account;
import unsl.entities.ResponseError;
import unsl.entities.User;
import unsl.entities.UserAccounts;
import unsl.services.UserService;
import unsl.utils.RestService;

@RestController
public class UserController {

    
    @Autowired
    UserService userService;
    
    @Autowired
    RestService  restService;

    @GetMapping(value = "/users")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
       return userService.getAll();
    }

    @GetMapping(value = "/users/{userId}")

    @ResponseBody
    public Object getUser(@PathVariable("userId") Long userId) {
        User user = userService.getUser(userId);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("User %d not found", userId)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @GetMapping(value = "/users/search")
    @ResponseBody
    public Object searchUser(@RequestParam("dni") Long dni) {
        User user = userService.findByDni(dni);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("User with dni %d not found", dni)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @PostMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object createUser(@RequestBody User User) {
        return userService.saveUser(User);
    }

    @PutMapping(value = "/users/{userId}")
    @ResponseBody
    public Object updateUser(@PathVariable("userId") long id_user, @RequestBody User user) {
        user.setId(id_user);
        User res = userService.updateUser(user);
        if (res == null) {
            return new ResponseEntity(new ResponseError(404, String.format("User with ID %d not found", id_user)), HttpStatus.NOT_FOUND);
        }
        return res;
    }

    @DeleteMapping(value = "/users/{userId}")
    @ResponseBody
    public Object deleteUser(@PathVariable("userId") Long id) throws Exception {
        User res = userService.deleteUser(id);
        if(res == null){
            return new ResponseEntity(new ResponseError(404,String.format("User with ID %d not found", id)), HttpStatus.NOT_FOUND);
        }

         UserAccounts allAccounts = restService.getAccounts(String.format("http://3.85.25.114:8889/accounts/search?holder=%d",res.getId()));
         /** hacer un patch a http://localhost:8889/accounts/{id} del status de cada cuenta a baja */
         
         for(Account deletedAccount: allAccounts.getUserAccounts()){
                                                                /* aca le paso por url el ?_method=patch porque despues uso postForObject para
                                                                evitar el error del patchforobject*/
            restService.updateAccountStatus(String.format("http://3.85.25.114:8889/accounts/%d?_method=patch",deletedAccount.getId()), deletedAccount);
         }
        return new ResponseEntity(null,HttpStatus.NO_CONTENT);
    }

}

