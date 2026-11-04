package com.esporteclubepelotas.api.modules.user.helper;

import com.esporteclubepelotas.api.common.exception.BusinessException;

import java.util.Map;

public final class UserHierarchyHelper {

    private static final Map<String, Integer> ROLE_LEVELS = Map.of(
            "OWNER", 100,
            "ADMIN", 80,
            "EDITOR", 50,
            "SOCIO", 20,
            "USER", 10
    );

    private UserHierarchyHelper() {}

    public static int getRoleLevel(String roleName) {
        if (roleName == null) return 0;
        String clean = normalizeRoleName(roleName);
        return ROLE_LEVELS.getOrDefault(clean, 0);
    }

    public static String normalizeRoleName(String roleName) {
        if (roleName == null) return "USER";
        String upper = roleName.trim().toUpperCase();
        if ("SOCIETY".equals(upper)) return "SOCIO";
        return upper;
    }

    public static void checkHierarchy(String actorRole, String targetRole, String actionType) {
        String actor = normalizeRoleName(actorRole);
        String target = normalizeRoleName(targetRole);

        int actorLevel = getRoleLevel(actor);
        int targetLevel = getRoleLevel(target);

        // O OWNER possui controle absoluto
        if ("OWNER".equals(actor)) {
            if ("delete".equalsIgnoreCase(actionType) && "OWNER".equals(target)) {
                throw new BusinessException("O Proprietário (Owner) não pode ser excluído.");
            }
            return;
        }

        // Regra 1: Qualquer tentativa de ação sobre o OWNER por não-owners é estritamente bloqueada
        if ("OWNER".equals(target)) {
            String verb = "promote".equalsIgnoreCase(actionType) ? "promover para"
                    : "delete".equalsIgnoreCase(actionType) ? "excluir um" : "modificar um";
            throw new BusinessException("Usuários do nível " + actor + " não têm permissão para " + verb + " Proprietário (Owner).");
        }

        // Regra 2: Usuários com nível menor NUNCA podem modificar usuários de nível maior
        if ("edit".equalsIgnoreCase(actionType) && actorLevel < targetLevel) {
            throw new BusinessException("Usuários do nível " + actor + " não têm permissão para modificar um usuário " + target + " (hierarquia superior).");
        }

        // Regra 3: Promoção/atribuição de cargo só pode ser feita para níveis estritamente inferiores ao do autor
        if ("promote".equalsIgnoreCase(actionType) && targetLevel >= actorLevel) {
            throw new BusinessException("Você não tem permissão para atribuir um cargo de nível igual ou superior ao seu (" + target + ").");
        }

        // Regra 4: Exclusão de usuários de mesmo nível ou superior é restrita ao OWNER
        if ("delete".equalsIgnoreCase(actionType) && targetLevel >= actorLevel) {
            throw new BusinessException("Apenas o Proprietário (Owner) pode excluir usuários do nível " + target + ".");
        }
    }
}

