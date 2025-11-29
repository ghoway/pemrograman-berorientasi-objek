CREATE DATABASE IF NOT EXISTS tb_pbo;
USE tb_pbo;

-- Tabel untuk menyimpan data siswa/mahasiswa
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nim VARCHAR(50) NOT NULL UNIQUE,
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    no_hp VARCHAR(20)
);

-- Tabel untuk menyimpan jadwal absen
CREATE TABLE IF NOT EXISTS schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(20) NOT NULL UNIQUE, -- e.g., 'Senin', 'Selasa'
    time_in TIME NOT NULL,
    time_out TIME NOT NULL
);

-- Tabel untuk menyimpan record absensi
CREATE TABLE IF NOT EXISTS attendances (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    attendance_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'TEPAT WAKTU', 'TERLAMBAT', 'PULANG AWAL'
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Insert default schedule (bisa diubah lewat aplikasi)
INSERT IGNORE INTO schedules (day_of_week, time_in, time_out) VALUES
('Senin', '07:30:00', '13:30:00'),
('Selasa', '07:30:00', '13:30:00'),
('Rabu', '07:30:00', '13:30:00'),
('Kamis', '07:30:00', '13:30:00'),
('Jumat', '07:00:00', '11:30:00');
