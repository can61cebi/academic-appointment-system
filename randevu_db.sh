#!/bin/bash
# PostgreSQL veritabanı oluşturma betiği

# Veritabanını oluştur (yoksa)
sudo -u postgres psql -c "CREATE DATABASE randevu_db;" || true

# 'randevu' kullanıcısı oluştur (yoksa) ve şifre ata
sudo -u postgres psql -c "DO \$\$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'randevu') THEN
        CREATE USER randevu WITH PASSWORD 'sifre';
    ELSE
        ALTER USER randevu WITH PASSWORD 'sifre';
    END IF;
END
\$\$;" || true

# Veritabanı üzerinde gerekli tabloları oluştur
sudo -u postgres psql -d randevu_db << EOF
-- Kullanıcılar tablosu (tüm kullanıcı tipleri için)
CREATE TABLE IF NOT EXISTS kullanicilar (
    id SERIAL PRIMARY KEY,
    kullanici_adi VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    sifre_hash VARCHAR(255) NOT NULL,
    ad VARCHAR(50) NOT NULL,
    soyad VARCHAR(50) NOT NULL,
    kullanici_tipi VARCHAR(20) NOT NULL CHECK (kullanici_tipi IN ('ogrenci', 'ogretim_uyesi', 'yetkili')),
    olusturma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Öğrenci bilgileri tablosu
CREATE TABLE IF NOT EXISTS ogrenciler (
    id SERIAL PRIMARY KEY,
    kullanici_id INTEGER UNIQUE REFERENCES kullanicilar(id) ON DELETE CASCADE,
    ogrenci_no VARCHAR(20) UNIQUE NOT NULL,
    bolum VARCHAR(100) NOT NULL
);

-- Öğretim üyesi bilgileri tablosu
CREATE TABLE IF NOT EXISTS ogretim_uyeleri (
    id SERIAL PRIMARY KEY,
    kullanici_id INTEGER UNIQUE REFERENCES kullanicilar(id) ON DELETE CASCADE,
    unvan VARCHAR(30) NOT NULL,
    bolum VARCHAR(100) NOT NULL,
    ofis VARCHAR(50)
);

-- Öğretim üyesi müsaitlik saatleri
CREATE TABLE IF NOT EXISTS musaitlik (
    id SERIAL PRIMARY KEY,
    ogretim_uyesi_id INTEGER REFERENCES ogretim_uyeleri(id) ON DELETE CASCADE,
    gun_adi VARCHAR(20) NOT NULL CHECK (gun_adi IN ('Pazartesi', 'Salı', 'Çarşamba', 'Perşembe', 'Cuma')),
    baslangic_saat TIME NOT NULL,
    bitis_saat TIME NOT NULL,
    CONSTRAINT baslangic_bitis_check CHECK (baslangic_saat < bitis_saat)
);

-- Randevu tablosu
CREATE TABLE IF NOT EXISTS randevular (
    id SERIAL PRIMARY KEY,
    ogrenci_id INTEGER REFERENCES ogrenciler(id) ON DELETE CASCADE,
    ogretim_uyesi_id INTEGER REFERENCES ogretim_uyeleri(id) ON DELETE CASCADE,
    tarih DATE NOT NULL,
    baslangic_saat TIME NOT NULL,
    bitis_saat TIME NOT NULL,
    durum VARCHAR(20) NOT NULL DEFAULT 'beklemede' CHECK (durum IN ('beklemede', 'onaylandı', 'reddedildi')),
    konu VARCHAR(255) NOT NULL,
    notlar TEXT,
    olusturma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT randevu_saati_check CHECK (bitis_saat > baslangic_saat)
);

-- Bildirimler tablosu
CREATE TABLE IF NOT EXISTS bildirimler (
    id SERIAL PRIMARY KEY,
    alici_id INTEGER REFERENCES kullanicilar(id) ON DELETE CASCADE,
    mesaj TEXT NOT NULL,
    okundu BOOLEAN DEFAULT FALSE,
    olusturma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Veritabanı sahipliğini randevu kullanıcısına ver
ALTER DATABASE randevu_db OWNER TO randevu;

-- Yetkili kullanıcı için izinler
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO randevu;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO randevu;
GRANT ALL PRIVILEGES ON SCHEMA public TO randevu;

-- Gelecekteki tablolar için de izinler
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO randevu;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO randevu;

-- Tüm tabloların sahipliğini randevu kullanıcısına ver
DO
\$\$
DECLARE
    tbl text;
BEGIN
    FOR tbl IN SELECT tablename FROM pg_tables WHERE schemaname = 'public'
    LOOP
        EXECUTE 'ALTER TABLE public.' || quote_ident(tbl) || ' OWNER TO randevu';
    END LOOP;
END
\$\$;

-- Tüm sequences sahipliğini randevu kullanıcısına ver
DO
\$\$
DECLARE
    seq text;
BEGIN
    FOR seq IN SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public'
    LOOP
        EXECUTE 'ALTER SEQUENCE public.' || quote_ident(seq) || ' OWNER TO randevu';
    END LOOP;
END
\$\$;
EOF

echo "Veritabanı yapısı ve izinler başarıyla güncellendi."