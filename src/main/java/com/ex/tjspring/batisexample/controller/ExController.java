package com.ex.tjspring.batisexample.controller;

import com.ex.tjspring.batisexample.dto.ExDto;
import com.ex.tjspring.batisexample.service.ExService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/batisexample")
public class ExController {

    @Autowired
    private ExService service;

    @GetMapping("/insert")
    public String insertPostForm(Model model) {
        model.addAttribute("exDto", new ExDto());
        return "/batisexample/insert";
    }

    @PostMapping("/insert")
    public String insertPostDB(@ModelAttribute ExDto exDto, Model model, RedirectAttributes redirectAttributes) throws Exception {
        service.exInsert(exDto);
        redirectAttributes.addFlashAttribute("msg", "게시글이 성공적으로 등록되었습니다!");
        return "redirect:/batisexample/list";
    }

    @GetMapping(value = "/list")
    public String exList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model) throws Exception {

        pageSize = 3;

        int offset = (page - 1) * pageSize;
        List<ExDto> dtos = service.exSelectAllPaged(offset, pageSize);

        int totalCount = service.exSelectTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        int pageBlockSize = 5;
        int startPage = ((page - 1) / pageBlockSize) * pageBlockSize + 1;
        int endPage = Math.min(startPage + pageBlockSize - 1, totalPages);

        model.addAttribute("list", dtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "/batisexample/list";
    }

    @GetMapping("/view/{id}")
    public String detailView(@PathVariable Long id ,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int pageSize,
                             Model model) throws Exception{
        ExDto dto = service.exSelectId(id);
        model.addAttribute("dto", dto);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        return "/batisexample/view";
    }

    @GetMapping(value = "/edit/{id}")
    public String update(@PathVariable Long id , Model model) throws Exception{
        ExDto dto = service.exSelectId(id);
        model.addAttribute("dto", dto);
        return "/batisexample/edit";
    }

    @PostMapping(value="/edit" )
    public String updateDB(@ModelAttribute ExDto dto,Model model , RedirectAttributes rttr) throws Exception {
        service.exUpdate(dto);
        rttr.addFlashAttribute("msg", "게시글이 수정되었습니다.");
        return "redirect:/batisexample/list";
    }

    @PostMapping("/delete/{id}")
    public String exDelete(@PathVariable Long id , RedirectAttributes rtts){
        service.exDelete(id);
        rtts.addFlashAttribute("msg", "게시글이 삭제되었습니다.");
        return "redirect:/batisexample/list";
    }
}



