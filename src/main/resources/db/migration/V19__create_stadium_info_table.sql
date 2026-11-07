-- V19: Criação da tabela de informações, memorial e grande matéria do Estádio Boca do Lobo (Parque Barbosa Leão)
CREATE TABLE IF NOT EXISTS public.stadium_info (
    id bigint NOT NULL PRIMARY KEY,
    title character varying(255) NOT NULL,
    subtitle character varying(500),
    cover_image_url text,
    inauguration_date character varying(100),
    capacity character varying(100),
    pitch_dimensions character varying(100),
    address character varying(255),
    maps_direct_url text,
    maps_embed_url text,
    content_html text NOT NULL,
    gallery_json jsonb NOT NULL DEFAULT '[]'::jsonb,
    facts_json jsonb NOT NULL DEFAULT '[]'::jsonb,
    is_active boolean NOT NULL DEFAULT true,
    created_at timestamp(6) with time zone NOT NULL DEFAULT NOW(),
    updated_at timestamp(6) with time zone NOT NULL DEFAULT NOW()
);

-- Seed inaugural com a história grandiosa, dados reais e acervo histórico da Boca do Lobo
INSERT INTO public.stadium_info (
    id,
    title,
    subtitle,
    cover_image_url,
    inauguration_date,
    capacity,
    pitch_dimensions,
    address,
    maps_direct_url,
    maps_embed_url,
    content_html,
    gallery_json,
    facts_json,
    is_active,
    created_at,
    updated_at
)
VALUES (
    1,
    'Estádio Boca do Lobo: A Fortaleza Centenária do Futebol Gaúcho',
    'Fundado em 1908, o mais antigo estádio de futebol em atividade do Rio Grande do Sul é o solo sagrado onde a paixão áureo-cerúlea desafia o tempo há mais de 115 anos.',
    'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1600&q=85',
    '25 de Outubro de 1908',
    '23.336 espectadores',
    '105m x 68m (Padrão Oficial)',
    'Avenida Bento Gonçalves com Gonçalves Chaves, Centro - Pelotas/RS, CEP 96015-140',
    'https://www.google.com/maps/search/?api=1&query=Est%C3%A1dio+Boca+do+Lobo+Pelotas',
    'https://maps.google.com/maps?q=Est%C3%A1dio+Boca+do+Lobo,+Pelotas+-+RS&t=&z=16&ie=UTF8&iwloc=&output=embed',
    '<h2>O Berço da Tradição e as Origens do Parque Barbosa Leão</h2>
<p>No início do século XX, quando o futebol dava seus primeiros passos no extremo sul do Brasil, a cidade de Pelotas já respirava cultura, efervescência industrial e paixão esportiva. Em 11 de outubro de 1908 nascia o Esporte Clube Pelotas, fruto da união entre o Club Sportivo Pelotense e o Club Sportivo Federal. Poucos dias após sua fundação oficial, a diretoria áureo-cerúlea deu o passo mais audacioso de sua história: a instalação do seu campo esportivo em uma área nobre da cidade, o histórico Parque Barbosa Leão, na confluência das avenidas Bento Gonçalves e Gonçalves Chaves.</p>
<p>Naquela época, campos de futebol eram espaços improvisados. O Pelotas, no entanto, projetou uma verdadeira praça desportiva cercada por eucaliptos, com arquibancadas de madeira erguidas com esforço comunitário dos próprios fundadores e torcedores. Em 25 de outubro de 1908 era realizada a primeira partida no local, inaugurando a praça que se tornaria o mais antigo estádio de futebol em atividade contínua de todo o Rio Grande do Sul e um dos mais velhos do país.</p>

<blockquote class="stadium-quote">"A Boca do Lobo não é apenas concreto, grama e arquibancada. É um templo vivo onde cada palmo de chão guarda o eco de gerações de famílias pelotenses que aprenderam a amar o manto azul e amarelo."</blockquote>

<h2>A Gênese da Mística: Por que "Boca do Lobo"?</h2>
<p>Com o passar dos anos e a consolidação do Pelotas como potência do futebol do interior, o estádio ganhou reformas estruturais que transformaram o Parque Barbosa Leão em um autêntico caldeirão. As arquibancadas foram aproximadas do retângulo de jogo, criando uma atmosfera de pressão acústica e visual sem igual na região.</p>
<p>A alcunha popular <strong>"Boca do Lobo"</strong> surgiu de forma espontânea na crônica desportiva e na boca do povo: entrar naquele estádio significava adentrar a toca da fera. Adversários acostumados a jogar em descampados encontravam em Pelotas uma torcida apaixonada, barulhenta e pulsante a poucos metros das linhas laterais. Visitar o Pelotas era ser devorado pela pressão da torcida áureo-cerúlea. O apelido foi abraçado com orgulho pela nação azul e amarela e hoje é a mais célebre identidade do clube.</p>

<h2>O Pioneirismo da Iluminação Noturna (1930)</h2>
<p>O ano de 1930 marcou para sempre a história do Esporte Clube Pelotas e da Boca do Lobo. Além de sagrar-se Campeão Gaúcho com uma campanha lendária, o clube inaugurou os imponentes refletores para partidas noturnas, um feito vanguardista que colocou Pelotas na liderança tecnológica do futebol sul-brasileiro, sendo o primeiro estádio do interior do estado a receber iluminação elétrica permanente.</p>
<p>A estreia dos jogos sob as luzes da Boca do Lobo parou a cidade. Famílias inteiras trajando seus melhores vestidos e ternos ocuparam os pavilhões sociais para testemunhar o espetáculo noturno. A noite pelotense nunca mais foi a mesma: o futebol passava a ser a grande celebração sob os céus do Rio Grande.</p>

<h2>Palco de Craques Mundiais e Noites Inesquecíveis</h2>
<p>Ao longo de mais de um século, o gramado da Boca do Lobo foi testemunha de momentos que figuram nas mais nobres páginas da história do futebol brasileiro. Grandes esquadrões nacionais e internacionais desfilaram pelo estádio: a Seleção Brasileira, o lendário Santos de Pelé nas décadas de 60, expedições de clubes europeus e os duelos memoráveis contra Grêmio e Internacional.</p>
<p>Mas é no clássico Bra-Pel que a Boca do Lobo atinge seu clímax de intensidade. Considerado por especialistas como uma das maiores e mais ferrenhas rivalidades do continente, o clássico municipal transforma o estádio em um mar de bandeiras azuis e amarelas, fumaça, cantos que não cessam e uma atmosfera eletrizante que transcende o esporte.</p>

<h2>Patrimônio Histórico, Caldeirão Moderno e o Futuro</h2>
<p>Hoje, o Estádio Boca do Lobo é muito mais do que um local de jogos: é um patrimônio imaterial da cidade de Pelotas. Com capacidade para mais de 20 mil torcedores, o estádio abriga a Loja Oficial Lobo Mania, a secretaria executiva, academia, departamento médico e as categorias de base do clube.</p>
<p>A cada domingo de jogo, a Avenida Bento Gonçalves se fecha para o trânsito e se abre para o coração do torcedor pelotense. Crianças de mãos dadas com seus avós cruzam os portões de ferro batido para ocupar o mesmo cimento onde gerações passadas choraram de emoção e comemoraram triunfos inesquecíveis. A Boca do Lobo segue firme, imponente e altiva: a verdadeira fortaleza do Lobo.</p>',
    jsonb_build_array(
        jsonb_build_object(
            'url', 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&w=1200&q=80',
            'caption', 'Vista aérea panorâmica do Estádio Boca do Lobo em tarde de clássico regional.',
            'order', 1
        ),
        jsonb_build_object(
            'url', 'https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80',
            'caption', 'O gramado impecável da Boca do Lobo sob os refletores históricos.',
            'order', 2
        ),
        jsonb_build_object(
            'url', 'https://images.unsplash.com/photo-1522778119026-d647f0596c20?auto=format&fit=crop&w=1200&q=80',
            'caption', 'Arquibancadas do estádio coloridas de azul e amarelo em dia de celebração.',
            'order', 3
        ),
        jsonb_build_object(
            'url', 'https://images.unsplash.com/photo-1518091043644-c1d4457512c6?auto=format&fit=crop&w=1200&q=80',
            'caption', 'Túnel de acesso dos atletas ao campo sagrado da Boca do Lobo.',
            'order', 4
        )
    ),
    jsonb_build_array(
        jsonb_build_object(
            'year', '1908',
            'title', 'Inauguração da Praça',
            'description', 'Aquisição do terreno no Parque Barbosa Leão e primeira partida oficial em 25 de outubro de 1908.'
        ),
        jsonb_build_object(
            'year', '1911',
            'title', 'O Primeiro Bra-Pel',
            'description', 'Início da centenária rivalidade que faz tremer os alicerces da Bento Gonçalves.'
        ),
        jsonb_build_object(
            'year', '1930',
            'title', 'Iluminação & Título Gaúcho',
            'description', 'Inauguração pioneira dos refletores noturnos e a conquista do Campeonato Gaúcho.'
        ),
        jsonb_build_object(
            'year', '1961',
            'title', 'Grandes Amistosos Nacionais',
            'description', 'O estádio recebe equipes históricas do cenário nacional e o esquadrão santista.'
        ),
        jsonb_build_object(
            'year', '2026',
            'title', 'Patrimônio Vivo',
            'description', 'Mais de 115 anos de atividade contínua como a casa inexpugnável do Lobo da Boca do Lobo.'
        )
    ),
    true,
    NOW(),
    NOW()
)
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    subtitle = EXCLUDED.subtitle,
    updated_at = NOW();

