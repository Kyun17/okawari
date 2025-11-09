package dev.okawari.controller;

import dev.okawari.dto.UserDTO;
import dev.okawari.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 페이지 뷰를 반환합니다.
     * @return "login.html"
     */
    @GetMapping("/login")
    public String loginPage(Model model, String error) {
        // 로그인 실패 시 (error=true) 메시지를 모델에 추가
        if (error != null) {
            model.addAttribute("loginError", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return "login"; // templates/login.html
    }

    /**
     * 로그인 폼 데이터를 처리합니다.
     * @param loginRequest (email, password)
     * @param request (HttpSession을 얻기 위함)
     * @return 로그인 성공 시 메인, 실패 시 로그인 페이지로 리다이렉트
     */
    @PostMapping("/login")
    public String loginProcess(@ModelAttribute UserDTO.LoginRequest loginRequest, HttpServletRequest request) {

        UserDTO.SessionUser sessionUser = userService.login(loginRequest);

        if (sessionUser == null) {
            // 로그인 실패
            return "redirect:/login?error=true";
        }

        // 로그인 성공. 세션을 가져와서 사용자 정보를 저장
        // (true): 세션이 없으면 새로 생성
        HttpSession session = request.getSession(true);
        session.setAttribute("loggedInUser", sessionUser);

        return "redirect:/"; // 메인 페이지로 리다이렉트
    }

    /**
     * 로그아웃을 처리합니다.
     * @param request (HttpSession을 가져오기 위함)
     * @return 메인 페이지로 리다이렉트
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false); // 세션이 없으면 새로 생성하지 않음

        if (session != null) {
            session.invalidate(); // 세션 파기
        }

        return "redirect:/";
    }

    // --- 회원가입 메서드 ---

    /**
     * 회원가입 페이지 뷰를 반환합니다.
     * @return "signup.html"
     */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        // th:object를 위해 빈 DTO 객체를 모델에 추가
        model.addAttribute("signupRequest", new UserDTO.SignupRequest());
        return "signup";
    }

    /**
     * 회원가입 폼 데이터를 처리합니다.
     * @param request (email, password, passwordConfirm, nickname)
     * @param redirectAttributes 리다이렉트 시 메시지 전달용
     * @return 성공 시 로그인 페이지, 실패 시 회원가입 페이지로 리다이렉트
     */
    @PostMapping("/signup")
    public String signupProcess(@ModelAttribute UserDTO.SignupRequest request, RedirectAttributes redirectAttributes) {

        String signupError = userService.signup(request);

        if (signupError != null) {
            // 회원가입 실패. 에러 메시지와 함께 폼으로 리다이렉트
            String errorMessage = "";
            switch (signupError) {
                case "passwordMismatch":
                    errorMessage = "비밀번호가 일치하지 않습니다.";
                    break;
                case "duplicateEmail":
                    errorMessage = "이미 사용 중인 이메일입니다.";
                    break;
                case "duplicateNickname":
                    errorMessage = "이미 사용 중인 닉네임입니다.";
                    break;
                default:
                    errorMessage = "알 수 없는 오류가 발생했습니다.";
            }
            // FlashAttribute: 리다이렉트 후에도 1회성으로 데이터를 전달
            redirectAttributes.addFlashAttribute("error", errorMessage);
            // (참고) 사용자가 입력했던 폼 데이터를 유지하고 싶다면 FlashAttribute로 request DTO도 넘겨줄 수 있습니다.
            return "redirect:/signup";
        }

        // 회원가입 성공
        redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/login";
    }
}