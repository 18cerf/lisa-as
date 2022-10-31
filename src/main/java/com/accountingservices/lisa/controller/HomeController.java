package com.accountingservices.lisa.controller;

import com.accountingservices.lisa.bot.Bot;
import com.accountingservices.lisa.models.UserRequest;
import com.accountingservices.lisa.repository.UserRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private UserRequestRepository userRequestRepository;


//    @Autowired
//    private Bot bot;

    @ModelAttribute("organizationTypes")
    public List<String> organizationTypes(Model model) {
        List<String> organizationTypes = new ArrayList<>();
        organizationTypes.add("ООО");
        organizationTypes.add("ИП");
        organizationTypes.add("Другое");
        return organizationTypes;
    }


    @ModelAttribute("userRequest")
    public UserRequest userRequest() {
        return new UserRequest();
    }

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @PostMapping("/")
    public String add(
            @Valid
//            @ModelAttribute("userRequest")
                    UserRequest userRequest,
            Errors errors,
            Model model
    ) {
        if (errors.hasErrors()) {
            model.addAttribute("message", "Ошибка в отправке заявки!");
            return "index";
        }
        model.addAttribute("message", "Заявка успешшно отправлена!");

        userRequestRepository.save(userRequest);


        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        Bot bot = new Bot();                  //We moved this line out of the register method, to access it later
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        bot.sendText(userRequest.toString());
        return "index";
    }


}