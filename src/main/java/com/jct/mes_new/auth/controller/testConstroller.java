package com.jct.mes_new.auth.controller;

import com.jct.mes_new.biz.system.vo.MemberVo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
public class testConstroller {


    /**
     * Get API
     */
    @GetMapping("/get")
    public String  getName() {
        /**
         *  넘겨 받는 값이 없을 경우
         */
        return "testConstroller";
    }

    /**
     * @PathVariable 방식
     * @param valiabe1
     * @return
     */
    @GetMapping("/getValiabe1/{valiable}")
    public String getValiable1(@PathVariable String valiabe1) {
        /**
         * {변수} 명과  @PathVariable에 있는 변수명을 일지
         */
        return valiabe1;
    }

    /**
     * 다른 변수명을 @PathVariable에 일치시키는 방법
     * @param var
     * @return
     */
    @GetMapping("/getValiabe2/{valiable}")
    public String getValiable2(@PathVariable("valiable") String var) {
        /**
         * {변수} 명과  @PathVariable에 변수명을 일치 시키술 없을 경우
         */
        return var;
    }

    //http:www.dddd.com/requsts?name=dfasdf&email=asdfasd@gmail.com&phone=111111111 형식
    @GetMapping("/request")
    public String getRequestParams1(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String phone

                                    ) {
        /**
         * 위 url 에서 ? 으로 넘어올 경우 사용
         */
        return name+email+phone;
    }

    //http:www.dddd.com/requsts?name=dfasdf&email=asdfasd@gmail.com&phone=111111111 형식
    @GetMapping("/request2")
    public String getRequestParams2(@RequestParam Map<String, String> param) {
        /**
         * 위 url 에서 어떤값으로 넘어올지 모르는 경우 map형식으로  받기
         */
        StringBuffer sb = new StringBuffer();

        param.entrySet().forEach(map -> {
            sb.append(map.getKey()).append(":").append(map.getValue()).append("\n");
        });

        return sb.toString();
    }

    @GetMapping("/request3")
    public String getRequestParams3(MemberVo memberVo) {
        /**
         *   key value가 정해져 있고 key값이 vo와 같을 경우
         */

        return memberVo.toString();
    }


    /**
     * Post API
     * @param mep
     * @return
     */
    @PostMapping("/savePost")
    public String savePost(@RequestBody Map<String, String> mep) {

        StringBuffer sb = new StringBuffer();
        mep.entrySet().forEach(map -> {
            sb.append(map.getKey()).append(":").append(map.getValue()).append("\n");
        });
        return sb.toString();
    }

    @PostMapping("/savePost1")
    public String savePost1(@RequestBody MemberVo memberVo) {
        return memberVo.toString();
    }


    /**
     *  PUT API
     */
    @PutMapping("/savePut1")
    public String savePut1(@RequestBody MemberVo memberVo) {
        return memberVo.toString();
    }
    @PutMapping("/savePut2")
    public MemberVo savePut2(@RequestBody MemberVo memberVo) {
        return memberVo;
    }
    @PutMapping("/savePut3")
    public ResponseEntity savePut3(@RequestBody MemberVo memberVo) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(memberVo);
    }

    /**
     * Delete API
     */
    @DeleteMapping("/delete")
    public String deleteA(@PathVariable String id) {
        return id;
    }

    /**
     *  ResponseEntity
     *  HttpEntity라는 클래스를 상속받아 사용하는 클래스
     *  사용자의 HttpREquest에 대한 응답 데이터를 포함
     *  포함하는 클래스 ( HttpStatus, HttpHeaders, HttpBody
     */



}
