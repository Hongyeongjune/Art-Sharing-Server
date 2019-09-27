package kr.ac.skuniv.artsharing.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import kr.ac.skuniv.artsharing.domain.dto.member.MemberGetDto;
import kr.ac.skuniv.artsharing.domain.dto.member.MemberUpdateDto;
import kr.ac.skuniv.artsharing.domain.dto.member.SignUpDto;
import kr.ac.skuniv.artsharing.domain.dto.member.SignInDto;
import kr.ac.skuniv.artsharing.domain.entity.Member;
import kr.ac.skuniv.artsharing.domain.roles.MemberRole;
import kr.ac.skuniv.artsharing.service.member.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/artSharing/sign")
public class MemberController {

    private final SignUpService signUpService;
    private final SignInService signInService;
    private final MemberGetService memberGetService;
    private final MemberUpdateService memberUpdateService;
    private final MemberDeleteService memberDeleteService;

    public MemberController(SignUpService signUpService, SignInService signInService, MemberGetService memberGetService, MemberUpdateService memberUpdateService, MemberDeleteService memberDeleteService) {
        this.signUpService = signUpService;
        this.signInService = signInService;
        this.memberGetService = memberGetService;
        this.memberUpdateService = memberUpdateService;
        this.memberDeleteService = memberDeleteService;
    }

    @ApiOperation(value = "고객 회원가입")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signUpDto", value="가입할 고객의 정보", required = true, dataType = "SignUpDto")
    })
    @PostMapping("/client")
    public ResponseEntity<Member> client_signUp(@RequestBody SignUpDto signUpDto) {
        return ResponseEntity.ok(signUpService.signUp(signUpDto, MemberRole.CLIENT));
    }

    @ApiOperation(value = "예술가 회원가입")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signUpDto", value="가입할 예술가의 정보", required = true, dataType = "SignUpDto")
    })
    @PostMapping("/artist")
    public ResponseEntity<Member> artist_signUp(@RequestBody SignUpDto signUpDto) {
        return ResponseEntity.ok(signUpService.signUp(signUpDto, MemberRole.ARTIST));
    }

    @ApiOperation(value = "로그인")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signInDto", value="로그인할 회원의 정보", required = true, dataType = "SignInDto")
    })
    @PostMapping
    public ResponseEntity login(@RequestBody SignInDto signInDto, HttpServletResponse response) {
        signInService.signIn(signInDto,response);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "회원 정보 열람")
    @GetMapping
    public MemberGetDto getMemberInfo(HttpServletRequest request) {
        return memberGetService.getMemberInfo(request);
    }

    @ApiOperation(value = "회원 정보 수정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberUpdateDto", value="수정할 데이터", required = true, dataType = "MemberUpdateDto")
    })
    @PutMapping
    public ResponseEntity updateMember(HttpServletRequest request, @RequestBody MemberUpdateDto memberUpdateDto) {
        memberUpdateService.updateMember(request, memberUpdateDto);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "회원 탈퇴")
    @DeleteMapping
    public ResponseEntity removeMember(HttpServletRequest request) {
        memberDeleteService.deleteMember(request);
        return ResponseEntity.ok().build();
    }


    @ApiOperation(value = "ID 중복 체크(중복된 ID가 없으면 True)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value="중복 체크할 아이디", required = true, dataType = "string")
    })
    @PostMapping("/check")
    public boolean checkUserId(@RequestBody String userId){
        return signUpService.checkUserID(userId);
    }


    @ApiOperation(value = "로그아웃")
    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        signInService.logout(request);
        return ResponseEntity.ok().build();
    }

}