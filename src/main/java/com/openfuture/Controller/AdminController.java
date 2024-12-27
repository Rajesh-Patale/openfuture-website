package com.openfuture.Controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openfuture.Entity.*;
import com.openfuture.Exception.AdminNotFoundException;
import com.openfuture.Exception.UserNotFoundException;
import com.openfuture.Repository.ForgotPasswordOtpRepository;
import com.openfuture.Service.AdminService;
import com.openfuture.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;


    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;


    @PostMapping("/admin/verifyMail/{userEmail}")
    public ResponseEntity<String> forgotPassword(@PathVariable String userEmail) {
        logger.info("Received the request to reset password for email: {}", userEmail);
        try {
            if (adminService.getUserByEmail(userEmail) == null) {
                return ResponseEntity.status(404).body("User not found with email: " + userEmail);
            }
            String otp = adminService.requestPasswordReset(userEmail);
            emailService.sendEmail(userEmail, "Password Reset OTP","Your OTP for password reset is: " + otp);
            return ResponseEntity.ok("Password reset OTP has been sent to your email.");
        } catch (AdminNotFoundException e) {
            logger.error("User not found with this email: {}", userEmail);
            return ResponseEntity.status(404).body("User not found with email: " + userEmail);
        } catch (Exception e) {
            logger.error("An error occurred while processing the password reset request for email: {}", userEmail, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/admin/verifyForgotPasswordOtp")
    public ResponseEntity<String> validateForgotPasswordOtp(@RequestParam String otp, @RequestParam String userEmail) {
        logger.info("Received request to validate OTP: {} for email: {}", otp, userEmail);

        // Step 1: Check if the user exists
        try {
            Admin admin = adminService.getUserByEmail(userEmail);
        } catch (AdminNotFoundException e) {
            logger.error("User not found with email: {}", userEmail, e);
            return ResponseEntity.status(404).body("User not found with email: " + userEmail);
        }
        // Step 2: Validate the OTP
        try {
            ForgotPasswordOtp passOtp = otpRepository.findByOtp(otp)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

            // Check if OTP has expired
            LocalDateTime now = LocalDateTime.now();
            if (passOtp.getCreatedAt().plusMinutes(5).isBefore(now)) { // Assuming 5 minutes expiration time
                otpRepository.delete(passOtp); // Optional: Clean up expired OTP
                logger.error("Expired OTP: {} for email: {}", otp, userEmail);
                return ResponseEntity.status(400).body("OTP has expired");
            }
            // OTP is valid
            logger.info("OTP is valid for email: {}", userEmail);
            return ResponseEntity.ok("OTP is verified, You can set new password ");

        } catch (IllegalArgumentException e) {
            logger.error("Invalid OTP: {} for email: {}", otp, userEmail, e);
            return ResponseEntity.status(400).body("Invalid or expired OTP");
        } catch (Exception e) {
            logger.error("An unexpected error occurred while validating OTP: {} for email: {}", otp, userEmail, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/admin/resetPassword/{userEmail}")
    public ResponseEntity<String> resetPassword(@PathVariable("userEmail") String userEmail,
                                                @RequestBody PasswordResetRequest request) {
        String password = request.getPassword();  // Get the password from the request

        logger.info("Received request to reset password for email: {}", userEmail);
        try {
            // Step 1: Retrieve the user by email
            Admin admin = adminService.getUserByEmail(userEmail);
            // Step 2: Change the user's password
            adminService.changeUserPassword(admin, password);
            return ResponseEntity.ok("Password successfully reset");

        } catch (AdminNotFoundException e) {
            logger.error("User not found with email: {}", userEmail, e);
            return ResponseEntity.status(404).body("User not found with email: " + userEmail);
        } catch (Exception e) {
            logger.error("An error occurred while resetting password for email: {}", userEmail, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerAdmin(@RequestPart("adminData") String adminData)  {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the admin data from JSON
            Admin admin = objectMapper.readValue(adminData, Admin.class);

            // Handle profile picture upload if provided
//            if (multipartFile != null && !multipartFile.isEmpty()) {
//                String contentType = multipartFile.getContentType();
//                if (contentType != null && isValidImageType(contentType)) {
//                    admin.setProfilePicture(multipartFile.getBytes());
//                }else {
//                    return ResponseEntity.badRequest().body("Invalid profile picture format. Only JPEG and PNG are supported.");
//                }
//            } else {
//                admin.setProfilePicture(null);
//            }



            // Register the admin
            String message = adminService.registerAdmin(admin);
            return ResponseEntity.status(HttpStatus.OK).body("Successfully registered Admin");
        } catch (JsonProcessingException e) {
            logger.error("Error parsing admin data: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid admin data format.");
        } catch (IllegalArgumentException e) {
            logger.error("Error during registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    // Helper method to validate image types
//    private  boolean isValidImageType(String contentType) {
//        return !contentType.equalsIgnoreCase(".jpeg") &&
//                !contentType.equalsIgnoreCase(".png") &&
//                !contentType.equalsIgnoreCase(".jpg");
//    }

    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestParam String username, @RequestParam String password) {
        try {
            logger.info("Login attempt for username: {}", username);
            String result = adminService.loginAdmin(username, password);
            return ResponseEntity.status(HttpStatus.OK).body("Admin Login Successfully");
        } catch (RuntimeException e) {
            logger.error("Error occurred during login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/update/{adminId}")
    public ResponseEntity<?> updateAdmin(
            @PathVariable Long adminId,
            @RequestPart("adminData") String adminData) {
//        @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture

        try {
            // Convert JSON string to Admin object
            ObjectMapper objectMapper = new ObjectMapper();
            Admin adminDetails = objectMapper.readValue(adminData, Admin.class);

            // Update the admin
//            Admin updatedAdmin = adminService.updateAdmin(adminId, adminDetails, profilePicture);
            Admin updatedAdmin = adminService.updateAdmin(adminId, adminDetails);

            // Return the updated admin
            return ResponseEntity.ok(updatedAdmin);

        } catch (AdminNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse admin data: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Invalid admin data format");
        } catch (Exception e) {
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long adminId) {
        try {
            if (adminId == null) {
                throw new UserNotFoundException("Admin id cannot be null");
            }
            adminService.deleteAdmin(adminId);
            return ResponseEntity.status(HttpStatus.OK).body("successfully deleted");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found with id: " + adminId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
    @GetMapping("/getAdminById/{adminId}")public ResponseEntity<Admin> getAdminById(@PathVariable Long adminId)
    {    try {        Admin admin = adminService.getAdminById(adminId);
        return new ResponseEntity<>(admin, HttpStatus.OK);    }
    catch (AdminNotFoundException e)
    {        logger.error("Admin not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);    }
    catch (Exception e) {        logger.error("Error occurred while fetching admin: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);    }}

    @PostMapping("/JobPost")
    public ResponseEntity<String> jobPost(@RequestBody Admin admin)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        adminService.jobpost(admin);
        return ResponseEntity.status(HttpStatus.OK).body("Job posted successfully");
    }

    // to get All User forms

    @GetMapping("/form/getAllForms")
    public ResponseEntity<List<Form>> getAllForms() {

        try {
            logger.info("Received request to get all forms");
            return new ResponseEntity<>(adminService.getAllForms(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve forms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/forms/{formId}")
    public ResponseEntity<Form> getFormByFormId(@PathVariable Long formId) {
        try {
            logger.info("Received request to get form by ID: {}", formId);
            return new ResponseEntity<>(adminService.getFormByFormId(formId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve form: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

//

    @GetMapping("/job/getAllJobs")
    public ResponseEntity<List<Job>> getAllJobsUploadedByAdmin() {
        try {
            logger.info("Received request to get all jobs uploaded by Admin ID: {}");
            return new ResponseEntity<>(adminService.getAllJobsUploadedByAdmin(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to retrieve jobs uploaded by admin: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
