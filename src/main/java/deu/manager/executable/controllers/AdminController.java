package deu.manager.executable.controllers;

import deu.manager.executable.domain.ClassStaff;
import deu.manager.executable.domain.Major;
import deu.manager.executable.domain.Professor;
import deu.manager.executable.domain.Student;
import deu.manager.executable.services.AdminRegistrationService;
import io.swagger.annotations.Api;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Api(tags = {"Admin"})
public class AdminController {

    private final AdminRegistrationService adminService;

    @Autowired
    public AdminController(AdminRegistrationService adminService) {
        this.adminService = adminService;
    }

    /**
     * consumes = MediaType.APPLICATION_JSON_VALUE
     * => 요청을 JSON TYPE의 데이터만 담고있는 요청을 처리하겠다는 의미가 된다.
     * 출처: https://yoonemong.tistory.com/227 [Sw.Dev]
     * ResponseEntity 는 status field를 가지기 때문에 상태코드는 필수적으로 리턴해줘야 한다.
     */
    @PostMapping(value = "/student", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateStudentResponse> createStudent(
            @RequestBody(required = true) CreateStudentRequest requestBody
            ) {
        Student saved = adminService.stuRegister(
                    requestBody.name,
                    requestBody.studentNum,
                    requestBody.residentNum,
                    requestBody.majorId.longValue());


        return ResponseEntity.ok(
                CreateStudentResponse.builder()
                        .id(saved.getId()) // 저장된 객체의 DB_ID를 반환
                        .name(saved.getName())
                        .studentNum(saved.getStudentNum())
                        .residentNum(saved.getResidentNum())
                        .major(Major.builder().id(saved.getMajor().getId()).name(saved.getMajor().getName()).build())
                        .build()
        );
    }

    /**
     * JSON 형식을 날라오는 request를 받기 위한 Class는 반드시
     * 기본 생성자 와 getter 메소드가 필요하다.
     * Class 변수의 이름은 JSON에서 보내는 Key,Value 중에서
     * Key 값과 일치 해야한다.
     */
    //왜 static 을 붙여서 하는지, 다시 한번 이유를 생각해보자
        // JSON으로 받을때 DataType이 세분화 되어있지 않고 단순히 string, int로만 구성되어 있으서 Long일 필요하지만 integer로 받는지 확인
        // 추후에 int를 long으로 바꿀때 int로써는 Long으로 바꾸지 못하기 때문에 처음부터 Integer로 받도록 한다.
    @NoArgsConstructor @Setter
    public static class CreateStudentRequest{
        String name;
        Integer studentNum;
        String residentNum;
        Integer majorId;
    }

    @Builder @Getter
    public static class CreateStudentResponse{
        Long id;
        String name;
        Integer studentNum;
        String residentNum;
        Major major;
    }

    /**
     * 교수들 등록하기 위한 Controller
     * @param createProfRequest
     * @return
     */
    @PostMapping(value="/prof" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateProfResponse> createProf(
            @RequestBody(required = true) CreateProfRequest createProfRequest
        ){

        Professor saved = adminService.profRegister(
                createProfRequest.name,
                createProfRequest.profNum,
                createProfRequest.residentNum,
                createProfRequest.majorId.longValue());

        return ResponseEntity.ok(CreateProfResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .profNum(saved.getProfessorNum())
                .residentNum(saved.getResidentNum())
                .major(Major.builder().id(saved.getMajor().getId()).name(saved.getMajor().getName()).build())
                .build());
    }

    /**
     * 교수를 등록하기 위한 요청DTO
     */
    @NoArgsConstructor @Getter
    public static class CreateProfRequest{
        String name;
        Integer profNum;
        String residentNum;
        Integer majorId;
    }

    /**
     * 교수를 등록하기 위한 응답DTO
     */
    @Builder @Getter
    public static class CreateProfResponse{
        Long id;
        String name;
        Integer profNum;
        String residentNum;
        Major major;
    }

    /**
     * 수업 담당자를 등록하기 위한 controller
     * @param createClassStaffRequest
     * @return
     */
    @PostMapping("/classStaff")
    public ResponseEntity<CreateClassStaffResponse> createClassStaff(
            @RequestBody(required = true) CreateClassStaffRequest createClassStaffRequest
    ){
        ClassStaff saved = adminService.staffRegister(
                createClassStaffRequest.name,
                createClassStaffRequest.staffNum,
                createClassStaffRequest.residentNum
        );

        return ResponseEntity.ok(
                CreateClassStaffResponse.builder()
                        .id(saved.getId())
                        .name(saved.getName())
                        .staffNum(saved.getStaffNum())
                        .residentNum(saved.getResidentNum())
                        .build()
        );


    }

    /**
     * 수업 담당자 등록을 위한 요청DTO
     */
    @NoArgsConstructor @Getter
    public static class CreateClassStaffRequest{
        String name;
        Integer staffNum;
        String residentNum;
    }

    /**
     * 수업 담당자 등록을 위한 응답DTO
     */
    @Builder @Getter
    public static class CreateClassStaffResponse{
        Long id;
        String name;
        Integer staffNum;
        String residentNum;
    }


}
