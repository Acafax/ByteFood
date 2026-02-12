package org.example.controllers;

import org.example.dtos.menu.RestaurantMenuDto;
import org.example.services.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
public class MenuController {


    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping()
    ResponseEntity<RestaurantMenuDto> getMenu(){
        RestaurantMenuDto menu = menuService.getMenu();
        return ResponseEntity.ok(menu);
    }



}
