package com.esporteclubepelotas.api.db;

import com.esporteclubepelotas.api.modules.football.service.FootballSeeder;
import com.esporteclubepelotas.api.modules.idol.entity.*;
import com.esporteclubepelotas.api.modules.idol.repository.IdolRepository;
import com.esporteclubepelotas.api.modules.news.entity.*;
import com.esporteclubepelotas.api.modules.news.repository.NewsArticleRepository;
import com.esporteclubepelotas.api.modules.news.repository.NewsCategoryRepository;
import com.esporteclubepelotas.api.modules.role.entity.Role;
import com.esporteclubepelotas.api.modules.role.repository.RoleRepository;
import com.esporteclubepelotas.api.modules.setting.service.SettingService;
import com.esporteclubepelotas.api.modules.systemmodule.entity.SystemModule;
import com.esporteclubepelotas.api.modules.systemmodule.repository.SystemModuleRepository;
import com.esporteclubepelotas.api.modules.theme.service.ThemeService;
import com.esporteclubepelotas.api.modules.user.entity.AddressItem;
import com.esporteclubepelotas.api.modules.user.entity.PhoneItem;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.entity.UserPreferences;
import com.esporteclubepelotas.api.modules.user.entity.UserStatus;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final SystemModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final IdolRepository idolRepository;
    private final NewsCategoryRepository newsCategoryRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ThemeService themeService;
    private final SettingService settingService;
    private final FootballSeeder footballSeeder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedModules();
        seedUsers();
        seedIdols();
        seedNews();
        footballSeeder.seedDefaultFootball();
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
                SystemModule.builder().name("Configurações").slug("settings").description("Personalização e temas do clube").isActive(true).build(),
                SystemModule.builder().name("Hino Oficial").slug("anthem").description("Hino oficial, letras, versões de áudio e história dos compositores").isActive(true).build(),
                SystemModule.builder().name("Símbolos e Mascote").slug("symbols").description("Mascote oficial, histórico de mascotes, escudos históricos, bandeira e cores áureo-cerúleas").isActive(true).build()
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

        List<PhoneItem> phones = new ArrayList<>();
        phones.add(PhoneItem.builder()
                .number("(53) 99999-0001")
                .isWhatsapp(true)
                .isPrimary(true)
                .isVerified(true)
                .build());

        List<AddressItem> addresses = new ArrayList<>();
        addresses.add(AddressItem.builder()
                .zipCode("96015-560")
                .street("Avenida Bento Gonçalves")
                .number("1908")
                .complement("Estádio Boca do Lobo")
                .neighborhood("Centro")
                .city("Pelotas")
                .state("RS")
                .isPrimary(true)
                .build());

        UserPreferences preferences = UserPreferences.builder()
                .shirtSize("G")
                .receiveNewsletter(true)
                .receiveSms(false)
                .build();

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
                .phone("(53) 99999-0001")
                .phones(phones)
                .addresses(addresses)
                .preferences(preferences)
                .build();

        userRepository.save(user);
        log.info("✅ [DatabaseSeeder] Usuário {} ({}) cadastrado: {} / Senha: admin123", firstName, roleName, email);
    }

    private void seedIdols() {
        if (idolRepository.count() > 0) return;

        log.info("🏛️ [DatabaseSeeder] Cadastrando Ídolos Históricos do Memorial...");

        // 1. Flávio Minuano (Jogador)
        Idol flavio = Idol.builder()
                .name("Flávio Almeida da Silva")
                .nickname("Flávio Minuano")
                .slug("flavio-minuano")
                .category(IdolCategory.PLAYER)
                .photoUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=800&q=80")
                .gallery(List.of(
                        "https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80"
                ))
                .galleryItems(List.of(
                        new IdolGalleryItem("https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=800&q=80", "Minuano comemorando na Boca do Lobo"),
                        new IdolGalleryItem("https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80", "Pelotas em campo na década de 60")
                ))
                .startYear(1961)
                .endYear(1974)
                .periods(List.of(new IdolPeriod(1961, 1974, "Primeira e Gloriosa Passagem")))
                .isActiveInClub(false)
                .birthDate(LocalDate.of(1944, 7, 9))
                .deathDate(LocalDate.of(2020, 5, 18))
                .birthPlace("Pelotas - RS")
                .quote("A Boca do Lobo sempre foi o templo onde meu coração batia no ritmo da torcida áureo-cerúlea.")
                .biography("Um dos maiores atacantes da história do futebol brasileiro. Revelado pelo Pelotas, Flávio Minuano encantou a torcida com sua potência física, cabeceio mortal e faro de gol inigualável. Disputou mais de 300 partidas com a camisa do Lobo e levou o nome de Pelotas para o cenário nacional.")
                .playerStats(IdolPlayerStats.builder()
                        .position("Centroavante")
                        .preferredFoot("Destro")
                        .historicJerseyNumber(9)
                        .matchesCount(312)
                        .goalsCount(148)
                        .titles(List.of("Campeonato Citadino de Pelotas", "Copa Governador do Estado"))
                        .memorableMatch("Pelotas 3x1 no clássico local em 1964 com 3 gols de Minuano na Boca do Lobo.")
                        .build())
                .isFeatured(true)
                .displayOrder(1)
                .isActive(true)
                .build();

        // 2. Luiz Carlos Winck (Treinador)
        Idol winck = Idol.builder()
                .name("Luiz Carlos Winck")
                .nickname("Professor Winck")
                .slug("luiz-carlos-winck")
                .category(IdolCategory.COACH)
                .photoUrl("https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=800&q=80")
                .gallery(List.of("https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80"))
                .galleryItems(List.of(new IdolGalleryItem("https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80", "Winck levantando a taça na Boca do Lobo")))
                .startYear(2018)
                .endYear(2021)
                .periods(List.of(new IdolPeriod(2018, 2021, "Era das Conquistas")))
                .isActiveInClub(false)
                .birthDate(LocalDate.of(1963, 1, 5))
                .birthPlace("Portão - RS")
                .quote("Comandar o Lobo na Boca do Lobo exige alma, entrega e garra do primeiro ao último minuto.")
                .biography("Comandante de uma das eras mais vitoriosas e consistentes do Pelotas nos últimos anos. Luiz Carlos Winck montou equipes aguerridas, taticamente impecáveis e conquistou títulos que marcaram a memória recente do torcedor áureo-cerúleo.")
                .coachStats(IdolCoachStats.builder()
                        .matchesCount(86)
                        .winsCount(44)
                        .drawsCount(26)
                        .lossesCount(16)
                        .titles(List.of("Copa FGF - Troféu Seu Verardi 2019", "Recopa Gaúcha 2020"))
                        .tacticalStyle("Transição rápida com compactação defensiva feroz e posse agressiva.")
                        .build())
                .isFeatured(true)
                .displayOrder(2)
                .isActive(true)
                .build();

        // 3. Seu Chico (Funcionário)
        Idol chico = Idol.builder()
                .name("Francisco Silveira")
                .nickname("Seu Chico")
                .slug("seu-chico")
                .category(IdolCategory.STAFF)
                .photoUrl("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=800&q=80")
                .startYear(1985)
                .isActiveInClub(true)
                .birthDate(LocalDate.of(1956, 3, 22))
                .birthPlace("Pelotas - RS")
                .quote("As camisas do Pelotas não são tecido, são a segunda pele da nossa cidade.")
                .biography("Mais de 35 anos dedicados diariamente ao vestiário do Esporte Clube Pelotas. Seu Chico é o guardião dos mantos sagrados, figura carismática amada por todas as gerações de atletas que passaram pela Boca do Lobo.")
                .staffStats(IdolStaffStats.builder()
                        .roleName("Roupeiro Histórico & Chefe de Vestiário")
                        .yearsOfDedication("Mais de 38 anos ininterruptos no clube")
                        .backstageStory("Lavou e preparou pessoalmente os uniformes das conquistas estaduais e copas do Lobo.")
                        .build())
                .isFeatured(true)
                .displayOrder(3)
                .isActive(true)
                .build();

        // 4. Dona Carmen da Charanga (Torcedora Símbolo)
        Idol carmen = Idol.builder()
                .name("Carmen Lúcia dos Santos")
                .nickname("Dona Carmen da Charanga")
                .slug("dona-carmen-da-charanga")
                .category(IdolCategory.FAN)
                .photoUrl("https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=800&q=80")
                .startYear(1970)
                .isActiveInClub(true)
                .birthDate(LocalDate.of(1948, 11, 12))
                .birthPlace("Pelotas - RS")
                .quote("Na chuva, no vento ou no sol escaldante, a Boca do Lobo é a minha casa e o Pelotas minha religião.")
                .biography("Símbolo inconfundível da bancada áureo-cerúlea. Dona Carmen esteve presente em praticamente todas as partidas na Boca do Lobo e caravanas pelo Rio Grande do Sul, liderando a charanga e contagiando o estádio com sua paixão eterna.")
                .fanStats(IdolFanStats.builder()
                        .stadiumSector("Arquibancada Geral - Boca do Lobo")
                        .trademark("Bumbo azul e amarelo e chapéu tradicional de feltro")
                        .build())
                .isFeatured(true)
                .displayOrder(4)
                .isActive(true)
                .build();

        idolRepository.saveAll(List.of(flavio, winck, chico, carmen));
        log.info("✅ [DatabaseSeeder] 4 Ídolos Históricos cadastrados com sucesso no Memorial.");
    }

    private void seedNews() {
        if (newsCategoryRepository.count() > 0) return;

        log.info("🌱 [DatabaseSeeder] Criando Categorias e Matérias oficiais...");

        NewsCategory futebol = NewsCategory.builder()
                .name("Futebol Profissional")
                .slug("futebol-profissional")
                .description("Notícias sobre os jogos, treinos e elenco profissional do Lobo")
                .displayOrder(1)
                .isActive(true)
                .build();

        NewsCategory institucional = NewsCategory.builder()
                .name("Institucional")
                .slug("institucional")
                .description("Comunicados oficiais, diretoria e ações institucionais do clube")
                .displayOrder(2)
                .isActive(true)
                .build();

        NewsCategory base = NewsCategory.builder()
                .name("Categorias de Base")
                .slug("categorias-de-base")
                .description("O futuro do Lobo: formação de atletas do Sub-15 ao Sub-20")
                .displayOrder(3)
                .isActive(true)
                .build();

        NewsCategory social = NewsCategory.builder()
                .name("Quadro Social")
                .slug("quadro-social")
                .description("Benefícios, planos e campanhas exclusivas para sócios áureo-cerúleos")
                .displayOrder(4)
                .isActive(true)
                .build();

        newsCategoryRepository.saveAll(List.of(futebol, institucional, base, social));

        User author = userRepository.findByEmail("admin@esporteclubepelotas.com.br")
                .or(() -> userRepository.findAll().stream().findFirst())
                .orElse(null);

        if (author == null) {
            log.warn("⚠️ [DatabaseSeeder] Nenhum autor encontrado para seed de notícias.");
            return;
        }

        java.time.Instant now = java.time.Instant.now();

        java.util.Map<String, Object> tiptapDoc = java.util.Map.of(
                "type", "doc",
                "content", java.util.List.of(
                        java.util.Map.of(
                                "type", "paragraph",
                                "content", java.util.List.of(
                                        java.util.Map.of("type", "text", "text", "O Esporte Clube Pelotas conquistou uma grande vitória na tarde deste domingo na Boca do Lobo. Com apoio fervoroso da torcida áureo-cerúlea, o Lobo bateu o rival por 2 a 0 e assumiu a liderança.")
                                )
                        )
                )
        );

        NewsArticle mainArticle = NewsArticle.builder()
                .title("Pelotas vence o clássico Farroupilha por 2 a 0 na Boca do Lobo")
                .subtitle("Com gols no segundo tempo, o Lobo garantiu os três pontos diante de um grande público na Avenida.")
                .slug("pelotas-vence-o-classico-farroupilha-por-2-a-0-na-boca-do-lobo")
                .previousSlugs(new ArrayList<>())
                .coverImage("https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80")
                .coverCaption("Torcida áureo-cerúlea lotou a Boca do Lobo na grande vitória. (Foto: Assessoria / E.C. Pelotas)")
                .content(tiptapDoc)
                .summary("Vitória expressiva em casa com grande apoio da torcida áureo-cerúlea garante a liderança na competição estadual.")
                .category(futebol)
                .tags(List.of("#Gauchão", "#Lobo", "#BocaDoLobo"))
                .status(NewsStatus.PUBLISHED)
                .isFeatured(true)
                .featuredSlot(NewsFeaturedSlot.MAIN)
                .isPinned(true)
                .reporterName("Assessoria de Comunicação")
                .photographerName("Lucas Silva / Foto Pelotas")
                .author(author)
                .publishedAt(now.minusSeconds(3600))
                .gallery(List.of(
                        "https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=800&q=80",
                        "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80"
                ))
                .viewsCount(128L)
                .allowReactions(true)
                .allowComments(true)
                .reactions(NewsReactionsCounter.builder().wolf(42).fire(18).clap(35).heart(64).build())
                .isActive(true)
                .build();

        NewsArticle socialArticle = NewsArticle.builder()
                .title("Quadro Social lança campanha de anuidade com benefícios exclusivos para sócios")
                .subtitle("Sócios em dia terão prioridade na compra de ingressos e descontos oficiais na loja do clube.")
                .slug("quadro-social-lanca-campanha-de-anuidade-com-beneficios-exclusivos")
                .previousSlugs(new ArrayList<>())
                .coverImage("https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=1200&q=80")
                .content(tiptapDoc)
                .summary("Campanha traz condições especiais para regularização e novos sócios áureo-cerúleos.")
                .category(social)
                .tags(List.of("#QuadroSocial", "#SócioLobo"))
                .status(NewsStatus.PUBLISHED)
                .isFeatured(true)
                .featuredSlot(NewsFeaturedSlot.SECONDARY)
                .isPinned(false)
                .author(author)
                .publishedAt(now.minusSeconds(7200))
                .viewsCount(74L)
                .allowReactions(true)
                .allowComments(true)
                .reactions(NewsReactionsCounter.builder().wolf(15).fire(5).clap(20).heart(33).build())
                .isActive(true)
                .build();

        NewsArticle baseArticle = NewsArticle.builder()
                .title("Categorias de base do Pelotas iniciam preparação para a Copa FGF Sub-20")
                .subtitle("Comissão técnica seleciona novos talentos da região para integrar os elencos de formação.")
                .slug("categorias-de-base-do-pelotas-iniciam-preparacao-para-copa-fgf-sub-20")
                .previousSlugs(new ArrayList<>())
                .coverImage("https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?auto=format&fit=crop&w=1200&q=80")
                .content(tiptapDoc)
                .summary("A garotada do Lobo intensifica os treinamentos com foco no estadual de base.")
                .category(base)
                .tags(List.of("#BaseForte", "#Sub20", "#Lobo"))
                .status(NewsStatus.PUBLISHED)
                .isFeatured(true)
                .featuredSlot(NewsFeaturedSlot.SECONDARY)
                .isPinned(false)
                .author(author)
                .publishedAt(now.minusSeconds(14400))
                .viewsCount(52L)
                .allowReactions(true)
                .allowComments(true)
                .reactions(NewsReactionsCounter.builder().wolf(10).fire(8).clap(14).heart(19).build())
                .isActive(true)
                .build();

        newsArticleRepository.saveAll(List.of(mainArticle, socialArticle, baseArticle));
        log.info("✅ [DatabaseSeeder] Categorias e 3 Matérias oficiais cadastradas com sucesso (Manchetão e Secundários).");
    }
}
