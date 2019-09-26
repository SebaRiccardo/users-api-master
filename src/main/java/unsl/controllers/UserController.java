package unsl.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping(value = "/users")
    @ResponseBody
    public Object updateUser(@RequestBody User User) {
        User res = userService.updateUser(User);
        if (res == null) {
            return new ResponseEntity(new ResponseError(404, String.format("User with ID %d not found", User.getId())), HttpStatus.NOT_FOUND);
        }
        return res;
    }

    @DeleteMapping(value = "/users/{id}")
    @ResponseBody
    public Object deleteUser(@PathVariable("id") Long id) throws Exception {
        User res = userService.deleteUser(id);
        if(res == null){
            return new ResponseEntity(new ResponseError(404,String.format("User with ID %d not found", id)), HttpStatus.NOT_FOUND);
        }

         UserAccounts allAccounts = restService.getAccounts(String.format("http://localhost:8889/accounts/search?holder=%d",res.getId()));
         /** hacer un patch a http://localhost:8889/accounts/{id} del status de cada cuenta a baja */
         
         for(Account deletedAccount: allAccounts.getUserAccounts()){
                                                                /* aca le paso por url el ?_method=patch porque despues uso postForObject para
                                                                evitar el error del patchforobject*/
            restService.updateAccountStatus(String.format("http://localhost:8889/accounts/%d?_method=patch",deletedAccount.getId()), deletedAccount);
         }
        return new ResponseEntity(null,HttpStatus.NO_CONTENT);
    }

}

