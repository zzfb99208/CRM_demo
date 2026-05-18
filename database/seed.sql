-- CRM Demo Seed Data

-- Customer
INSERT INTO customer (customer_code, company_name, customer_tier, payment_terms, currency, incoterms, region_cover, distribution_type, term_of_agreement, assay_expiry_req, special_notes, status)
VALUES ('AMD', 'A. Menarini Diagnostics SRL', 'A', 'Net 60 days from invoice date', 'EUR', 'FCA', 'Italy, Netherlands, Belgium, Spain, France', 'Exclusive distributor', '2025-2027', 'not less than 6 months', 'Special Label and battery label', 1);

-- Customer Contacts
INSERT INTO customer_contact (customer_id, contact_name, title, email, phone, notes) VALUES
(1, 'Contact A', 'Global Marketing and Business Development Director', 'contactA@menarini.com', NULL, NULL),
(1, 'Contact B', 'Global Marketing Manager Molecular & Advanced Technologies', 'contactB@menarini.com', NULL, 'Marketing'),
(1, 'Contact C', 'Global Application Lead - Molecular Diagnostics', 'contactC@menarini.com', NULL, 'Marketing'),
(1, 'Contact D', NULL, 'contactD@menarini.com', NULL, 'ORDER');

-- Products (16 sample products)
INSERT INTO product (menarini_code, coyote_code, product_name, description, category, unit_price_kit, unit_price_test, dimension, net_weight_kg, gross_weight_kg) VALUES
('61312', '2004009001', 'FlashDetect Flash10 System', 'COYOTE FLASH10 SYSTEM', 'INSTRUMENT', 100, 14000, '57*45*55cm', 21.00, 33.00),
('59391', '2004009202', 'FlashDetect Nano System', 'COYOTE NANO SYSTEM', 'INSTRUMENT', 100, 7000, '57*45*55cm', 18.00, 28.00),
('59393', '6018006402-CE', 'FlashDetect LyocartD SARS-CoV-2 Assay', 'LCD-A SARS', 'ASSAY', 100, 8, '57*26*37cm', 1.40, 2.00),
('59394', '6018006405-CE', 'FlashDetect LyocartD SARS-CoV-2 Assay', 'LCD-B SARS', 'ASSAY', 100, 8, '57*26*37cm', 1.40, 2.00),
('59397', '6018006802-CE', 'FlashDetect LyocartD SARS-CoV-2&Flu A&Flu B Assay', 'MD.LCD-A SARS FLA FLB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59398', '6018006805-CE', 'FlashDetect LyocartD SARS-CoV-2&Flu A&Flu B Assay', 'MD.LCD-B SARS FLA FLB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59399', '6018007202-CE', 'FlashDetect LyocartE SARS-CoV-2&Flu A&Flu B Assay', 'LCE-A SARS FLUA FLUB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59400', '6018007205-CE', 'FlashDetect LyocartE SARS-CoV-2&Flu A&Flu B Assay', 'LCE-B SARS FLUA FLUB', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('59406', '6018012305-CE', 'FlashDetect LyocartD MG&TV&MH Assay', 'MD.LCD-B MG TV MH', 'ASSAY', 100, 10, '57*26*37cm', 1.40, 1.80),
('59407', '6018012502-CE', 'FlashDetect LyocartE MG&TV&MH Assay', 'MD.LCE-A MG TV MH', 'ASSAY', 100, 10, '57*26*37cm', 1.40, 1.80),
('59823', '6018006901-CE', 'FlashDetect SARS-CoV-2&FluA&FluB EQC (SPEC.A)', 'MD.EQC-A SARS FLA FLB', 'EQC', 100, 20, NULL, 0.10, NULL),
('59824', '6018006902-CE', 'FlashDetect SARS-CoV-2&FluA&FluB EQC (SPEC.B)', 'MD.EQC-B SARS FLA FLB', 'EQC', 100, 20, NULL, 0.10, NULL),
('59827', '6018010301-CE', 'FlashDetect MG&TV&MH EQC (SPEC. A)', 'MD.EQC-A MG TV MH', 'EQC', 100, 20, NULL, 0.10, NULL),
('59828', '6018010302-CE', 'FlashDetect MG&TV&MH EQC (SPEC. B)', 'MD.EQC-B MG TV MH', 'EQC', 100, 20, NULL, 0.10, NULL),
('61200', '6018037401-CE', 'COYOTE CCTS Sputum Liquefaction Kit', 'MD.SPUTUM LIQ KIT', 'CONSUMABLE', 100, 0.00, '57*26*37cm', 1.00, 1.20),
('61201', '6018032401-CE', 'COYOTE CCTS Microbial Sample Collection Kit', 'MD.MICROB COLL KIT', 'CONSUMABLE', 100, 0.00, '51*34*58cm', 1.80, 1.93),
('59786', '6018034205-EN', 'FlashDetect LyocartE CT&NG&UU Assay', 'MD.LCE-B CT NG UU', 'ASSAY', 100, 10, '57*26*37cm', 1.60, 2.10),
('61197', '6018013905-CE', 'FlashDetect LyocartE MTB Assay', 'MD.LCE-B MTB', 'ASSAY', 100, 9, '57*26*37cm', 1.40, 1.80),
('59429', '6018022502-CE', 'FlashDetect LyocartE Monkeypox Virus Assay', 'LCE-A MPOX', 'ASSAY', 100, 8, '57*26*37cm', 1.60, 2.10),
('59412', '6018011805-CE', 'FlashDetect LyocartD NG&UU Assay', 'MD.LCD-B NG UU', 'ASSAY', 100, 10, '57*26*37cm', 1.40, 1.80);

-- Inventory (5 batches)
INSERT INTO inventory (product_id, lot_no, expiry_date, quantity_kit, quantity_test) VALUES
(3, '20260108A', '2027-01-07', 50, 1200),
(3, '20251210C', '2026-12-09', 30, 720),
(7, '20260108D', '2027-01-07', 100, 2400),
(7, '20251210B', '2026-12-09', 80, 1920),
(8, '20260108A', '2027-01-07', 40, 960),
(1, '304090251212009', '2027-06-01', 10, 10);
