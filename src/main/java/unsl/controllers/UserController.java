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
    private static String ipCuentas="localhost";
    
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
            return new ResponseEntity(new ResponseError(404, String.format("Holder with id: %d not found", userId)), HttpStatus.NOT_FOUND);
        }
        



        return user;
    }

    @GetMapping(value = "/users/search")
    @ResponseBody
    public Object searchUser(@RequestParam("dni") Long dni) {
        User user = userService.findByDni(dni);
        if ( user == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Holder with dni: %d not found", dni)), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @PostMapping(value = "/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object createUser(@RequestBody User user) {
       
       if(user.getFirstName()!=null && user.getLastName()!=null && user.getDni() != null ){
          user.setStatus(User.Status.ACTIVO);
           return userService.saveUser(user);
        }else{
           return new ResponseEntity(new ResponseError(400, String.format("Holder data is missing ")), HttpStatus.BAD_REQUEST);    
        }   
    }

    @PutMapping(value = "/users/{userId}")
    @ResponseBody
    public Object updateUser(@PathVariable("userId") long id_user, @RequestBody User user) {
        
        User currentUser = userService.getUser(id_user);
       
        if (currentUser == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Holder with id: %d not found", id_user)), HttpStatus.NOT_FOUND);
        }
        //controla si los datos no son null
        if(user.getFirstName()==null && user.getLastName()==null ){
           return currentUser;
        }else{
           if(user.getFirstName()!= null && user.getLastName() !=null ){
                currentUser.setFirstName(user.getFirstName());
                currentUser.setLastName(user.getLastName());
           }else{
               // solo actualiza el dato q no es null
               if(user.getLastName()!= null){
                 currentUser.setLastName(user.getLastName());
               }else{
                 currentUser.setFirstName(user.getFirstName());
               }
           }   
        }
        return userService.saveUser(currentUser);
    }

    @DeleteMapping(value = "/users/{userId}")
    @ResponseBody
    public Object deleteUser(@PathVariable("userId") Long id) throws Exception {
        User res = userService.deleteUser(id);
        if(res == null){
            return new ResponseEntity(new ResponseError(404,String.format("Holder with id: %d not found", id)), HttpStatus.NOT_FOUND);
        }

         UserAccounts allAccounts = restService.getAccounts(String.format("http://"+ipCuentas+":8889/accounts/search?holder=%d",res.getId()));
         /** hacer un patch a http://localhost:8889/accounts/{id} del status de cada cuenta a baja */
         
         for(Account deletedAccount: allAccounts.getUserAccounts()){
                                                                /* aca le paso por url el ?_method=patch porque despues uso postForObject para
                                                                evitar el error del patchforobject*/
            deletedAccount.setStatus(Account.Status.BAJA);
            /// deberia controlar que el monto de cada cuenta sea cero pero por ahora voy a dejar que las de de BAJA con monto positivo
            restService.updateAccountStatus(String.format("http://"+ipCuentas+":8889/accounts/%d?_method=patch",deletedAccount.getId()), deletedAccount);
         }
        return new ResponseEntity(null,HttpStatus.NO_CONTENT);
    }

}

