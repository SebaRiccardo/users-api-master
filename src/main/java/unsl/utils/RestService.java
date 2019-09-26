package unsl.utils;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import unsl.entities.*;

@Service
public class RestService {
    
    /** 
     * @param url
     * @return
     * @throws Exception
     */
    public UserAccounts getAccounts(String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        UserAccounts accounts;
        try {
            accounts = restTemplate.getForObject(url,UserAccounts.class);
        }  catch (Exception e){
            throw new Exception( buildMessageError(e));
        }
        return accounts;
    }
     /** 
     * @param url
     * @return
     * @throws Exception
     */

    public Account updateAccountStatus(String url,Account updatedAccount) throws Exception{
             
        RestTemplate restTemplate = new RestTemplate();
        Account account;
        try {
         account = restTemplate.postForObject(url,updatedAccount,Account.class);
        }  catch (Exception e){
        throw new Exception( buildMessageError(e));
       }
        return account;
    }
   
    private String buildMessageError(Exception e) {
        String msg = e.getMessage();
        if (e instanceof HttpClientErrorException) {
            msg = ((HttpClientErrorException) e).getResponseBodyAsString();
        } else if (e instanceof HttpServerErrorException) {
            msg =  ((HttpServerErrorException) e).getResponseBodyAsString();
        }
        return msg;
    }

}

