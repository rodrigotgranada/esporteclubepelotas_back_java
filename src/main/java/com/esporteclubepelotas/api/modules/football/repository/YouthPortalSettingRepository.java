package com.esporteclubepelotas.api.modules.football.repository;

import com.esporteclubepelotas.api.modules.football.entity.YouthPortalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YouthPortalSettingRepository extends JpaRepository<YouthPortalSetting, Long> {
}

