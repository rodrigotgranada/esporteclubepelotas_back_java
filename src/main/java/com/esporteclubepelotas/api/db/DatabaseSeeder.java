package com.esporteclubepelotas.api.db;

import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import com.esporteclubepelotas.api.modules.setting.service.SettingService;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import com.esporteclubepelotas.api.modules.theme.service.ThemeService;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final SystemModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ThemeService themeService;
    private final SettingService settingService;

    @Override
    public void run(String... args) {
        seedRoles();
        seedModules();
        seedUsers();
        themeService.seedDefaultTheme();
        settingService.seedDefaultSettings();
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) return;

        log.info("🌱 [DatabaseSeeder] Criando Roles padrão do sistema...");

        Role owner = Role.builder()
                .name("OWNER")
                .label("Proprietário")
                .description("Controle irrestrito e absoluto de todo o portal e permissões")
                .permissions(List.of("*"))
                .isImmutable(true)
                .build();

        Role admin = Role.builder()
                .name("ADMIN")
                .label("Administrador")
                .description("Gestão completa de notícias, futebol, ídolos e torcedores")
                .permissions(List.of(
                        "news.manage",
                        "football.manage",
                        "idols.manage",
                        "users.manage",
                        "settings.manage"
                ))
                .isImmutable(true)
                .build();

        Role editor = Role.builder()
                .name("EDITOR")
                .label("Editor de Comunicação")
                .description("Redação e publicação de matérias, fotos e memorial")
                .permissions(List.of("news.manage", "idols.manage"))
                .isImmutable(false)
                .build();

        Role socio = Role.builder()
                .name("SOCIO")
                .label("Sócio Áureo-Cerúleo")
                .description("Acesso exclusivo a benefícios do quadro social e ingressos")
                .permissions(List.of("socio.read"))
                .isImmutable(false)
                .build();

        Role user = Role.builder()
                .name("USER")
                .label("Torcedor")
                .description("Acesso público a comentários, reações e perfil")
                .permissions(List.of())
                .isImmutable(false)
                .build();

        roleRepository.saveAll(List.of(owner, admin, editor, socio, user));
        log.info("✅ [DatabaseSeeder] 5 Roles cadastradas com sucesso.");
    }

    private void seedModules() {
        if (moduleRepository.count() > 0) return;

        log.info("🌱 [DatabaseSeeder] Criando Módulos do Sistema...");

        List<SystemModule> modules = List.of(
                SystemModule.builder().name("Usuários").slug("users").description("Gestão de torcedores e acessos").isActive(true).build(),
                SystemModule.builder().name("Matérias").slug("news").description("Portal de notícias e comunicação").isActive(true).build(),
                SystemModule.builder().name("Ídolos & Memorial").slug("idols").description("Lendas da Boca do Lobo").isActive(true).build(),
                SystemModule.builder().name("Futebol").slug("football").description("Plantel, atletas e comissão técnica").isActive(true).build(),
                SystemModule.builder().name("História").slug("history").description("Linha do tempo e patrimônio centenário").isActive(true).build(),
                SystemModule.builder().name("Configurações").slug("settings").description("Personalização e temas do clube").isActive(true).build()
        );

        moduleRepository.saveAll(modules);
        log.info("✅ [DatabaseSeeder] 6 Módulos do Sistema cadastrados com sucesso.");
    }

    private void seedUsers() {
        log.info("👥 [DatabaseSeeder] Verificando e criando usuários padrão de cada Role...");

        seedUserIfNotExists(
                "OWNER",
                "Proprietário",
                "Master",
                "admin@esporteclubepelotas.com.br",
                "00000000001",
                "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&auto=format&fit=crop&q=80"
        );

        seedUserIfNotExists(
                "ADMIN",
                "Gestor",
                "Administrativo",
                "gestor@esporteclubepelotas.com.br",
                "00000000002",
                "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80"
        );

        seedUserIfNotExists(
                "EDITOR",
                "Editor",
                "Notícias",
                "editor@esporteclubepelotas.com.br",
                "00000000003",
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80"
        );

        seedUserIfNotExists(
                "SOCIO",
                "Sócio",
                "Áureo-Cerúleo",
                "socio@esporteclubepelotas.com.br",
                "00000000004",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80"
        );

        seedUserIfNotExists(
                "USER",
                "Torcedor",
                "Lobo",
                "torcedor@esporteclubepelotas.com.br",
                "00000000005",
                "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150&auto=format&fit=crop&q=80"
        );
    }

    private void seedUserIfNotExists(String roleName, String firstName, String lastName, String email, String cpf, String avatarUrl) {
        if (userRepository.existsByEmail(email) || userRepository.existsByCpf(cpf)) {
            return;
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role " + roleName + " não encontrada para associar ao usuário " + email));

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .cpf(cpf)
                .passwordHash(passwordEncoder.encode("admin123"))
                .avatarUrl(avatarUrl)
                .role(role)
                .status(UserStatus.ACTIVE)
                .isActive(true)
                .emailVerified(true)
                .mustChangePassword(false)
                .birthDate(LocalDate.of(1908, 10, 11))
                .build();

        userRepository.save(user);
        log.info("✅ [DatabaseSeeder] Usuário {} ({}) cadastrado: {} / Senha: admin123", firstName, roleName, email);
    }
}
