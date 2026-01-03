package com.undira.tbwahyu.ui;

import com.undira.tbwahyu.MainApp;
import com.undira.tbwahyu.auth.User;
import com.undira.tbwahyu.config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

public class FormProyek extends JFrame {

    private final User currentUser;
    private Integer selectedId = null;

    // Header
    private JLabel lblJudul, lblNamaManajer;

    // Form fields
    private JTextField txtKodeProyek, txtNamaProyek, txtNamaManajer;
    private JTextField txtAnggaran, txtLokasi;
    private JTextArea txtKendala, txtSolusi;
    private JComboBox<String> cmbStatus;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    public FormProyek(User user) {
        this.currentUser = user;
        initComponents();
        setTitle("Sistem Informasi Manajemen Proyek - " + user.getRole());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        applyRoleRules();
        loadTableData();
    }

    /* ================= INIT ================= */

    private void initComponents() {

        /* ===== HEADER ===== */
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblJudul = new JLabel("SISTEM INFORMASI MANAJEMEN PROYEK", JLabel.CENTER);
        lblJudul.setFont(new Font("Arial", Font.BOLD, 20));
        lblNamaManajer = new JLabel("Nama Manajer: -", JLabel.CENTER);

        header.add(lblJudul);
        header.add(lblNamaManajer);

        /* ===== FORM ===== */
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Input Data Proyek"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        txtKodeProyek = new JTextField(15);
        txtNamaProyek = new JTextField(15);
        txtNamaManajer = new JTextField(15);
        txtAnggaran = new JTextField(15);
        txtLokasi = new JTextField(15);
        txtKendala = new JTextArea(3, 15);
        txtSolusi = new JTextArea(3, 15);

        txtKendala.setLineWrap(true);
        txtSolusi.setLineWrap(true);

        // ENUM STATUS
        cmbStatus = new JComboBox<>(new String[]{
                "DRAFT",
                "BERJALAN",
                "TERTUNDA",
                "SELESAI",
                "BATAL"
        });

        String[] labels = {
                "Kode Proyek", "Nama Proyek", "Nama Manajer",
                "Anggaran", "Lokasi", "Kendala", "Solusi", "Status"
        };

        JComponent[] fields = {
                txtKodeProyek,
                txtNamaProyek,
                txtNamaManajer,
                txtAnggaran,
                txtLokasi,
                new JScrollPane(txtKendala),
                new JScrollPane(txtSolusi),
                cmbStatus
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }

        JButton btnSimpan = new JButton("Simpan");
        JButton btnReset = new JButton("Reset");
        JButton btnHapus = new JButton("Hapus");
        JButton btnLogout = new JButton("Logout");

        btnSimpan.addActionListener(e -> simpanData());
        btnReset.addActionListener(e -> resetForm());
        btnHapus.addActionListener(e -> hapusData());
        btnLogout.addActionListener(e -> logout());

        gbc.gridy = labels.length;
        gbc.gridx = 0; form.add(btnSimpan, gbc);
        gbc.gridx = 1; form.add(btnReset, gbc);
        gbc.gridx = 2; form.add(btnHapus, gbc);
        gbc.gridx = 3; form.add(btnLogout, gbc);

        /* ===== TABLE ===== */
        String[] kolom = {
                "ID", "Kode", "Nama Proyek", "Manajer",
                "Anggaran", "Lokasi", "Status", "Kendala", "Solusi"
        };

        tableModel = new DefaultTableModel(kolom, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelectedRow();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Proyek"));

        setLayout(new BorderLayout(10, 10));
        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        setMinimumSize(new Dimension(1150, 700));
    }

    /* ================= LOAD ================= */

    private void loadTableData() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM proyek ORDER BY id DESC";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {

            while (r.next()) {
                Vector<Object> v = new Vector<>();
                v.add(r.getInt("id"));
                v.add(r.getString("kode_proyek"));
                v.add(r.getString("nama_proyek"));
                v.add(r.getString("nama_manajer"));
                v.add(r.getObject("anggaran"));
                v.add(r.getString("lokasi"));
                v.add(r.getString("status"));
                v.add(r.getString("kendala"));
                v.add(r.getString("solusi"));
                tableModel.addRow(v);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void loadSelectedRow() {
        int r = table.getSelectedRow();
        if (r == -1) return;

        selectedId = (Integer) tableModel.getValueAt(r, 0);

        txtKodeProyek.setText(val(r, 1));
        txtNamaProyek.setText(val(r, 2));
        txtNamaManajer.setText(val(r, 3));
        txtAnggaran.setText(val(r, 4));
        txtLokasi.setText(val(r, 5));
        cmbStatus.setSelectedItem(val(r, 6));
        txtKendala.setText(val(r, 7));
        txtSolusi.setText(val(r, 8));

        lblNamaManajer.setText("Nama Manajer: " + txtNamaManajer.getText());
    }

    private String val(int r, int c) {
        Object o = tableModel.getValueAt(r, c);
        return o == null ? "" : o.toString();
    }

    /* ================= SIMPAN ================= */

    private void simpanData() {
        String role = currentUser.getRole().toUpperCase();

        try (Connection c = DBConnection.getConnection()) {

            // ADMIN: INSERT DATA DASAR
            if (role.equals("ADMIN")) {
                if (txtKodeProyek.getText().isEmpty()
                        || txtNamaProyek.getText().isEmpty()
                        || txtNamaManajer.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Admin wajib mengisi Kode Proyek, Nama Proyek, dan Nama Manajer!");
                    return;
                }

                String sql = "INSERT INTO proyek (kode_proyek, nama_proyek, nama_manajer) VALUES (?,?,?)";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, txtKodeProyek.getText());
                p.setString(2, txtNamaProyek.getText());
                p.setString(3, txtNamaManajer.getText());
                p.executeUpdate();
            }

            // STAFF: UPDATE DETAIL + STATUS
            else if (role.equals("STAFF")) {
                if (selectedId == null) {
                    JOptionPane.showMessageDialog(this, "Pilih proyek terlebih dahulu!");
                    return;
                }

                String sql = "UPDATE proyek SET anggaran=?, lokasi=?, kendala=?, status=? WHERE id=?";
                PreparedStatement p = c.prepareStatement(sql);
                p.setDouble(1, Double.parseDouble(txtAnggaran.getText()));
                p.setString(2, txtLokasi.getText());
                p.setString(3, txtKendala.getText());
                p.setString(4, cmbStatus.getSelectedItem().toString());
                p.setInt(5, selectedId);
                p.executeUpdate();
            }

            // MANAGER: UPDATE SOLUSI
            else if (role.equals("MANAGER")) {
                if (selectedId == null) {
                    JOptionPane.showMessageDialog(this, "Pilih proyek terlebih dahulu!");
                    return;
                }

                String sql = "UPDATE proyek SET solusi=? WHERE id=?";
                PreparedStatement p = c.prepareStatement(sql);
                p.setString(1, txtSolusi.getText());
                p.setInt(2, selectedId);
                p.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Data berhasil diproses");
            resetForm();
            loadTableData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /* ================= UTIL ================= */

    private void resetForm() {
        selectedId = null;
        txtKodeProyek.setText("");
        txtNamaProyek.setText("");
        txtNamaManajer.setText("");
        txtAnggaran.setText("");
        txtLokasi.setText("");
        txtKendala.setText("");
        txtSolusi.setText("");
        cmbStatus.setSelectedItem("DRAFT");
        lblNamaManajer.setText("Nama Manajer: -");
    }

    private void applyRoleRules() {
        String r = currentUser.getRole().toUpperCase();

        txtKodeProyek.setEditable(r.equals("ADMIN"));
        txtNamaProyek.setEditable(r.equals("ADMIN"));
        txtNamaManajer.setEditable(r.equals("ADMIN"));

        txtAnggaran.setEditable(r.equals("STAFF"));
        txtLokasi.setEditable(r.equals("STAFF"));
        txtKendala.setEditable(r.equals("STAFF"));
        cmbStatus.setEnabled(r.equals("STAFF"));

        txtSolusi.setEditable(r.equals("MANAGER"));
    }

    private void hapusData() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus data proyek ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM proyek WHERE id=?")) {

            p.setInt(1, selectedId);
            p.executeUpdate();
            resetForm();
            loadTableData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void logout() {
        dispose();
        SwingUtilities.invokeLater(MainApp::showLogin);
    }
}
