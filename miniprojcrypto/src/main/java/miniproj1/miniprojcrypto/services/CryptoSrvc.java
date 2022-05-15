package miniproj1.miniprojcrypto.services;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import VTTP2022.NUSISS.MarchBinanceAPI.models.Crypto;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CryptoService {
    private static final Logger logger = LoggerFactory.getLogger(CryptoService.class);
    private static final String URL = "https://api.binance.com/api/v3/ticker/24hr";

    public List<Crypto> getAllCrypto(){

        String cryptoUrl = UriComponentsBuilder
        .fromUriString(URL)
        .toUriString();
        
        RequestEntity req = RequestEntity
        .get(cryptoUrl)
        .accept(MediaType.APPLICATION_JSON)
        .build();

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = null;

        List<Crypto> cryptoList = new LinkedList<>();
        try{
            resp = template.exchange(req, String.class);
            cryptoList = Crypto.create(resp.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return cryptoList;


    }

}
