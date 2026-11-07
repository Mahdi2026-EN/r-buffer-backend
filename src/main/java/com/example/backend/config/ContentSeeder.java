package com.example.backend.config;

import com.example.backend.entity.CmsContent;
import com.example.backend.entity.SiteSettings;
import com.example.backend.repository.CmsContentRepository;
import com.example.backend.repository.SiteSettingsRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Creates the starting website text and the default settings row on first start.
 * Rows that already exist are never overwritten, so anything an admin edits is kept.
 */
@Component
public class ContentSeeder implements ApplicationRunner {

    private final CmsContentRepository cmsContentRepository;
    private final SiteSettingsRepository siteSettingsRepository;

    public ContentSeeder(CmsContentRepository cmsContentRepository, SiteSettingsRepository siteSettingsRepository) {
        this.cmsContentRepository = cmsContentRepository;
        this.siteSettingsRepository = siteSettingsRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!siteSettingsRepository.existsById(SiteSettings.SINGLETON_ID)) {
            siteSettingsRepository.save(new SiteSettings());
        }

        add("home", "hero", "heroTitle", "Digital Products Made Simple", "محصولات دیجیتال به سادگی", "ډیجیټل محصولات په اسانۍ سره");
        add("home", "hero", "heroSubtitle", "A professional marketplace for digital products with a transparent platform credit system.", "یک بازار حرفه‌ای برای محصولات دیجیتال با سیستم شفاف اعتبار پلتفرم.", "د پلیټفارم کریډټ روښانه سیسټم سره د ډیجیټل محصولاتو لپاره یو مسلکي بازار.");
        add("about", "mission", "missionText", "R-Buffer provides a trusted environment for purchasing digital products using platform credit.", "R-Buffer یک محیط قابل اعتماد برای خرید محصولات دیجیتال با استفاده از اعتبار پلتفرم فراهم می‌کند.", "R-Buffer د پلیټفارم کریډټ په کارولو سره د ډیجیټل محصولاتو د پیرودلو لپاره یو باوري چاپیریال برابروي.");
        add("contact", "info", "email", "support@rbuffer.io", "support@rbuffer.io", "support@rbuffer.io");
        add("footer", "about", "aboutText", "R-Buffer is a digital product marketplace with an integrated platform credit system.", "R-Buffer یک بازار محصولات دیجیتال با سیستم اعتبار پلتفرم یکپارچه است.", "R-Buffer د یوځای شوي پلیټفارم کریډټ سیسټم سره د ډیجیټل محصولاتو بازار دی.");
    }

    private void add(String page, String section, String key, String en, String fa, String ps) {
        if (cmsContentRepository.findAllByOrderByPageAscSectionAscContentKeyAsc().stream()
                .anyMatch(c -> c.getPage().equals(page) && c.getSection().equals(section) && c.getContentKey().equals(key))) {
            return;
        }
        CmsContent content = new CmsContent(page, section, key, Map.of("en", en, "fa", fa, "ps", ps));
        cmsContentRepository.save(content);
    }
}
