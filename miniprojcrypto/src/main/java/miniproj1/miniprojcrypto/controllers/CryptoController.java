package miniproj1.miniprojcrypto.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import VTTP2022.NUSISS.MarchBinanceAPI.models.Crypto;
import VTTP2022.NUSISS.MarchBinanceAPI.services.CryptoService;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping
public class CryptoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CryptoController.class);

    @Autowired
    CryptoService cryptoSvc;
    
    @GetMapping
    public String getCrypto(Model model){
        List<Crypto> cryptoList = cryptoSvc.getAllCrypto();
       
        model.addAttribute("allCrypto", cryptoList);
        model.addAttribute("timestamp", new Date().toString());
        return "index";
    }

}
