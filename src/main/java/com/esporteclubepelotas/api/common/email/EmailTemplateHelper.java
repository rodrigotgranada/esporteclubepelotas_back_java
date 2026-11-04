package com.esporteclubepelotas.api.common.email;

public final class EmailTemplateHelper {

    private EmailTemplateHelper() {}

    private static String getBaseTemplate(String title, String contentHtml) {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>%s</title>
              <style>
                body { margin: 0; padding: 0; background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1e293b; }
                .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #e2e8f0; }
                .header { background-color: #0f2d59; padding: 32px 24px; text-align: center; }
                .logo-text { color: #fbc02d; font-size: 24px; font-weight: 800; letter-spacing: 1px; margin: 0; text-transform: uppercase; }
                .content { padding: 36px 32px; }
                .h1 { font-size: 20px; font-weight: 700; color: #0f2d59; margin-top: 0; margin-bottom: 16px; }
                .p { font-size: 15px; line-height: 1.6; color: #475569; margin-bottom: 20px; }
                .code-box { background-color: #f1f5f9; border-radius: 8px; padding: 20px; text-align: center; margin: 28px 0; border: 1px dashed #cbd5e1; }
                .code { font-size: 32px; font-weight: 800; letter-spacing: 6px; color: #0f2d59; font-family: monospace; }
                .btn { display: inline-block; background-color: #0f2d59; color: #fbc02d; text-decoration: none; padding: 14px 28px; border-radius: 8px; font-weight: 700; font-size: 15px; margin: 20px 0; text-align: center; }
                .footer { background-color: #f8fafc; padding: 24px; text-align: center; border-top: 1px solid #e2e8f0; font-size: 12px; color: #64748b; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1 class="logo-text">Esporte Clube Pelotas</h1>
                </div>
                <div class="content">
                  %s
                </div>
                <div class="footer">
                  <p>© Esporte Clube Pelotas — O Lobo da Boca do Lobo.<br>Este é um e-mail automático, favor não responder.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(title, contentHtml);
    }

    public static String getVerificationEmail(String code, boolean isResend) {
        String title = isResend ? "Novo Código de Verificação" : "Confirme seu Cadastro";
        String content = """
            <h2 class="h1">%s</h2>
            <p class="p">Olá, torcedor áureo-cerúleo!</p>
            <p class="p">Utilize o código de confirmação abaixo para validar seu e-mail e ativar sua conta no portal oficial do Esporte Clube Pelotas:</p>
            <div class="code-box">
              <span class="code">%s</span>
            </div>
            <p class="p">Este código é válido por <strong>60 minutos</strong>. Se você não solicitou este cadastro, desconsidere esta mensagem.</p>
            """.formatted(title, code);
        return getBaseTemplate(title, content);
    }

    public static String getResetPasswordEmail(String frontendUrl, String token) {
        String title = "Redefinição de Senha";
        String resetUrl = (frontendUrl != null ? frontendUrl : "http://localhost:7999") + "/reset-password?token=" + token;
        String content = """
            <h2 class="h1">Redefinição de Senha</h2>
            <p class="p">Recebemos uma solicitação para redefinir a senha da sua conta no portal do Esporte Clube Pelotas.</p>
            <p class="p">Clique no botão abaixo para escolher uma nova senha de acesso:</p>
            <div style="text-align: center;">
              <a href="%s" class="btn" style="color: #fbc02d !important;">Redefinir Minha Senha</a>
            </div>
            <p class="p">Ou acesse diretamente pelo link: <br><a href="%s" style="color: #0f2d59; word-break: break-all;">%s</a></p>
            <p class="p">Este link expira em <strong>60 minutos</strong>. Caso não tenha solicitado a redefinição, sua senha permanece segura e nenhuma ação é necessária.</p>
            """.formatted(resetUrl, resetUrl, resetUrl);
        return getBaseTemplate(title, content);
    }

    public static String getChangeContactEmail(String type, String code, String newValue) {
        String title = "Confirme a alteração de " + ("email".equalsIgnoreCase(type) ? "e-mail" : "celular");
        String content = """
            <h2 class="h1">Confirmação de Alteração</h2>
            <p class="p">Você solicitou a alteração do seu <strong>%s</strong> para: <strong>%s</strong>.</p>
            <p class="p">Informe o código abaixo para confirmar a alteração no seu perfil:</p>
            <div class="code-box">
              <span class="code">%s</span>
            </div>
            <p class="p">Este código expira em <strong>2 horas</strong>. Se você não realizou esta solicitação, entre em contato imediatamente com o suporte.</p>
            """.formatted("email".equalsIgnoreCase(type) ? "e-mail" : "celular", newValue, code);
        return getBaseTemplate(title, content);
    }
}

