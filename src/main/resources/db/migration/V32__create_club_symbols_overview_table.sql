-- Migration V32: Tabela de Textos Gerais / Narrativas Institucionais de Símbolos e name opcional
CREATE TABLE IF NOT EXISTS public.club_symbols_overview (
    category VARCHAR(30) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Permite que o campo name em club_symbols seja opcional (itens podem se identificar apenas pelo período)
ALTER TABLE public.club_symbols ALTER COLUMN name DROP NOT NULL;

-- Inserção das narrativas históricas gerais de Mascote, Distintivo e Pavilhão/Cores
INSERT INTO public.club_symbols_overview (category, title, description, updated_at)
VALUES (
    'MASCOT',
    'A Mística do Lobo da Boca do Lobo',
    'O Lobo é o símbolo máximo da alma, da garra e da lealdade áureo-cerúlea. A alcunha nasceu intimamente entrelaçada com a mística do Estádio Boca do Lobo — o palco de futebol mais antigo em atividade ininterrupta no Rio Grande do Sul e um dos monumentos do futebol brasileiro. Quando o Pelotas entra em campo, o Lobo sintetiza a força da matilha: uma torcida destemida e apaixonada que canta e defende o pavilhão azul e amarelo com honra.',
    NOW()
)
ON CONFLICT (category) DO UPDATE SET
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    updated_at = NOW();

INSERT INTO public.club_symbols_overview (category, title, description, updated_at)
VALUES (
    'SHIELD',
    'O Brasão Centenário & Tradição de 1908',
    'O distintivo do Esporte Clube Pelotas é composto pelas elegantes iniciais entrelaçadas "E.C.P." no interior de um escudo de formato suíço. As cores azul cerúleo e amarelo ouro refletem a grandeza do ideal e a tradição que atravessa os séculos desde 1908, coroada pela histórica conquista do Campeonato Gaúcho de 1930.',
    NOW()
)
ON CONFLICT (category) DO UPDATE SET
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    updated_at = NOW();

INSERT INTO public.club_symbols_overview (category, title, description, updated_at)
VALUES (
    'FLAG_COLORS',
    'O Pavilhão Sagrado & As Cores Áureo-Cerúleas',
    'O pavilhão oficial do Esporte Clube Pelotas tremula solenemente na Boca do Lobo em suas duas faixas horizontais de proporções iguais: a superior em amarelo ouro e a inferior em azul cerúleo, trazendo ao centro o brasão centenário. As cores áureo-cerúleas unem o amarelo ouro — evocando a nobreza, a energia solar e a glória imortal — e o azul celeste — remetendo à infinitude do céu de Pelotas e à lealdade inabalável da torcida.',
    NOW()
)
ON CONFLICT (category) DO UPDATE SET
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    updated_at = NOW();

