package com.esporteclubepelotas.api.modules.user.controller;

import com.esporteclubepelotas.api.common.security.CurrentUser;
import com.esporteclubepelotas.api.common.security.UserPrincipal;
import com.esporteclubepelotas.api.modules.auth.dto.UserSummaryDto;
import com.esporteclubepelotas.api.modules.user.dto.ConfirmChangeDto;
import com.esporteclubepelotas.api.modules.user.dto.RequestChangeDto;
import com.esporteclubepelotas.api.modules.user.dto.UpdateMeDto;
import com.esporteclubepelotas.api.modules.user.dto.UpdatePasswordDto;
import com.esporteclubepelotas.api.modules.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Operações do Perfil do Usuário Autenticado")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PatchMapping("/me")
    @Operation(summary = "Atualizar dados cadastrais do próprio perfil")
    public ResponseEntity<UserSummaryDto> updateMe(
            @RequestBody UpdateMeDto dto,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(userProfileService.updateMe(principal.getId(), dto));
    }

    @RequestMapping(value = "/me/avatar", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Atualizar foto de perfil (avatar) do próprio usuário")
    public ResponseEntity<Map<String, String>> updateMyAvatar(
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @CurrentUser UserPrincipal principal) {
        MultipartFile finalFile = avatar != null ? avatar : file;
        if (finalFile == null || finalFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nenhum arquivo enviado"));
        }
        return ResponseEntity.ok(userProfileService.updateAvatar(principal.getId(), finalFile));
    }

    @DeleteMapping("/me/avatar")
    @Operation(summary = "Remover foto de perfil (avatar)")
    public ResponseEntity<UserSummaryDto> deleteMyAvatar(@CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(userProfileService.deleteAvatar(principal.getId()));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Desativar a própria conta (exige confirmação de senha)")
    public ResponseEntity<Map<String, Boolean>> deactivateMe(
            @RequestBody Map<String, String> payload,
            @CurrentUser UserPrincipal principal) {
        String password = payload.get("password");
        userProfileService.deactivateMe(principal.getId(), password);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PatchMapping("/profile/password")
    @Operation(summary = "Alterar a senha da própria conta")
    public ResponseEntity<Map<String, Boolean>> updatePassword(
            @Valid @RequestBody UpdatePasswordDto dto,
            @CurrentUser UserPrincipal principal) {
        userProfileService.updatePassword(principal.getId(), dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/profile/request-change")
    @Operation(summary = "Solicitar alteração segura de e-mail ou celular (envia código OTP)")
    public ResponseEntity<UserSummaryDto> requestChange(
            @Valid @RequestBody RequestChangeDto dto,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(userProfileService.requestChange(
                principal.getId(), dto.getType(), dto.getNewValue(), dto.getCurrentPassword()));
    }

    @PostMapping("/profile/confirm-change")
    @Operation(summary = "Confirmar alteração de e-mail ou celular informando o código OTP")
    public ResponseEntity<UserSummaryDto> confirmChange(
            @Valid @RequestBody ConfirmChangeDto dto,
            @CurrentUser UserPrincipal principal) {
        return ResponseEntity.ok(userProfileService.confirmChange(
                principal.getId(), dto.getType(), dto.getCode()));
    }
}

