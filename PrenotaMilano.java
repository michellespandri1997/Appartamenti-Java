import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PrenotaMilano extends JFrame {
    private static final Color INK = new Color(34, 43, 39);
    private static final Color MUTED = new Color(112, 121, 115);
    private static final Color GREEN = new Color(35, 75, 61);
    private static final Color GREEN_DARK = new Color(24, 53, 44);
    private static final Color ACCENT = new Color(197, 111, 77);
    private static final Color CANVAS = new Color(246, 246, 241);
    private static final Color LINE = new Color(224, 227, 219);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Apartment[] APARTMENTS = {
        new Apartment("Brera Loft", "Brera · Milano centro", "Brera · central Milan", 4, 178,
            "Un punto di partenza nel cuore creativo di Milano.",
            "A base in the heart of Milan's creative quarter.",
            "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1100&q=85"),
        new Apartment("Navigli Studio", "Navigli · Darsena", "Navigli · Darsena", 2, 132,
            "La città da vivere tra i canali e le vie dei Navigli.",
            "Settle in by the canals and lively Navigli streets.",
            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1100&q=85")
    };

    private boolean english;
    private JComboBox<String> apartmentChoice;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JSpinner guestCount;
    private JTextField nameField;
    private JTextField emailField;
    private JTextArea noteField;
    private JLabel validationLabel;
    private JLabel requestFeedbackLabel;
    private JLabel quoteDetails;
    private JLabel quotePrice;
    private JLabel confirmationLabel;
    private JPanel quoteSection;
    private JPanel requestSection;
    private JButton calculateButton;
    private JButton requestButton;
    private JButton newSearchButton;
    private JButton italianButton;
    private JButton englishButton;
    private Quote currentQuote;
    private String requestCode;
    private boolean requestSent;

    public PrenotaMilano() {
        super("Milano / Stay");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 720));
        setSize(1180, 920);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        render();
    }

    private void render() {
        String selectedApartment = apartmentChoice == null ? APARTMENTS[0].name : (String) apartmentChoice.getSelectedItem();
        String savedCheckIn = checkInField == null ? LocalDate.now().plusDays(14).format(DATE_FORMAT) : checkInField.getText();
        String savedCheckOut = checkOutField == null ? LocalDate.now().plusDays(17).format(DATE_FORMAT) : checkOutField.getText();
        int savedGuests = guestCount == null ? 2 : (Integer) guestCount.getValue();
        String savedName = nameField == null ? "" : nameField.getText();
        String savedEmail = emailField == null ? "" : emailField.getText();
        String savedNote = noteField == null ? "" : noteField.getText();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CANVAS);
        root.add(buildNavigation(), BorderLayout.NORTH);

        JPanel page = new JPanel();
        page.setOpaque(false);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBorder(new EmptyBorder(20, 34, 32, 34));

        HeroPanel hero = new HeroPanel();
        hero.setLayout(new BorderLayout());
        JPanel heroCopy = new JPanel();
        heroCopy.setOpaque(false);
        heroCopy.setLayout(new BoxLayout(heroCopy, BoxLayout.Y_AXIS));
        heroCopy.setBorder(new EmptyBorder(28, 34, 26, 24));
        heroCopy.add(label(english ? "A CURATED STAY  ·  MILAN, ITALY" : "SOGGIORNI SELEZIONATI  ·  MILANO, ITALIA", 11, new Color(207, 219, 207), Font.BOLD));
        heroCopy.add(Box.createVerticalStrut(13));
        heroCopy.add(label(english ? "Make yourself at home in Milan." : "La tua Milano, da abitare.", 34, Color.WHITE, Font.BOLD, "Georgia"));
        heroCopy.add(Box.createVerticalStrut(9));
        heroCopy.add(label(english
            ? "Two apartments, two neighbourhoods to explore. Find your place in the city."
            : "Due appartamenti, due quartieri da vivere. Trova il tuo spazio in città.", 14, new Color(220, 228, 219), Font.PLAIN));
        hero.add(heroCopy, BorderLayout.CENTER);
        JLabel coordinates = label("45°28' N\n09°11' E", 13, new Color(224, 203, 176), Font.PLAIN, "Georgia");
        coordinates.setHorizontalAlignment(SwingConstants.RIGHT);
        coordinates.setBorder(new EmptyBorder(0, 0, 0, 34));
        hero.add(coordinates, BorderLayout.EAST);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 188));
        page.add(hero);
        page.add(Box.createVerticalStrut(25));
        page.add(sectionHeading(english ? "Stay somewhere special" : "Scegli il tuo spazio", english
            ? "Two addresses, each with its own way into the city." : "Due indirizzi, due modi diversi di vivere la città."));
        page.add(Box.createVerticalStrut(14));

        JPanel cardGrid = new JPanel(new GridLayout(1, 2, 16, 0));
        cardGrid.setOpaque(false);
        for (Apartment apartment : APARTMENTS) {
            cardGrid.add(buildApartmentCard(apartment));
        }
        cardGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 304));
        page.add(cardGrid);
        page.add(Box.createVerticalStrut(28));
        page.add(sectionHeading(english ? "Find your stay" : "Trova il tuo soggiorno", english
            ? "Choose dates and guests to see an estimated total." : "Scegli date e ospiti per calcolare il totale indicativo."));
        page.add(Box.createVerticalStrut(13));
        page.add(buildSearchPanel());
        apartmentChoice.setSelectedItem(selectedApartment);
        checkInField.setText(savedCheckIn);
        checkOutField.setText(savedCheckOut);
        guestCount.setValue(savedGuests);
        page.add(Box.createVerticalStrut(14));

        quoteSection = buildQuoteAndRequest();
        nameField.setText(savedName);
        emailField.setText(savedEmail);
        noteField.setText(savedNote);
        quoteSection.setVisible(currentQuote != null);
        page.add(quoteSection);

        JLabel footer = label(english
            ? "Independent demo. Availability, prices and requests are not connected to external services."
            : "Demo indipendente. Disponibilità, prezzi e richieste non sono collegati a servizi esterni.", 11, MUTED, Font.PLAIN);
        footer.setBorder(new EmptyBorder(18, 2, 0, 2));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        page.add(footer);

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CANVAS);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        root.add(scroll, BorderLayout.CENTER);
        setContentPane(root);
        updateQuoteLabels();
        revalidate();
        repaint();
    }

    private JPanel buildNavigation() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(new EmptyBorder(14, 34, 14, 34));
        JLabel brand = label("M / S   MILANO STAY", 13, GREEN, Font.BOLD);
        bar.add(brand, BorderLayout.WEST);
        JPanel languages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        languages.setOpaque(false);
        italianButton = languageButton("IT", !english);
        englishButton = languageButton("EN", english);
        italianButton.addActionListener((ActionEvent event) -> {
            if (english) {
                english = false;
                render();
            }
        });
        englishButton.addActionListener((ActionEvent event) -> {
            if (!english) {
                english = true;
                render();
            }
        });
        languages.add(italianButton);
        languages.add(englishButton);
        bar.add(languages, BorderLayout.EAST);
        return bar;
    }

    private JButton languageButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(selected ? Color.WHITE : MUTED);
        button.setBackground(selected ? GREEN : Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel buildApartmentCard(Apartment apartment) {
        SurfacePanel card = new SurfacePanel(Color.WHITE, LINE, 12);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(9, 9, 10, 9));
        PhotoPanel photo = new PhotoPanel(apartment.photoUrl, apartment == APARTMENTS[0]);
        photo.setPreferredSize(new Dimension(420, 142));
        card.add(photo, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBorder(new EmptyBorder(12, 7, 2, 7));
        JLabel name = label(apartment.name, 19, INK, Font.BOLD, "Georgia");
        name.setAlignmentX(LEFT_ALIGNMENT);
        details.add(name);
        JLabel area = label(english ? apartment.areaEn : apartment.areaIt, 12, MUTED, Font.PLAIN);
        area.setAlignmentX(LEFT_ALIGNMENT);
        details.add(Box.createVerticalStrut(3));
        details.add(area);
        JLabel description = label(english ? apartment.descriptionEn : apartment.descriptionIt, 12, INK, Font.PLAIN);
        description.setAlignmentX(LEFT_ALIGNMENT);
        details.add(Box.createVerticalStrut(8));
        details.add(description);
        details.add(Box.createVerticalGlue());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel capacity = label("●  " + (english ? "Up to " : "Fino a ") + apartment.capacity + (english ? " guests" : " ospiti"), 11, GREEN, Font.BOLD);
        bottom.add(capacity, BorderLayout.WEST);
        JLabel price = label((english ? "From  " : "Da  ") + euro(apartment.rate) + (english ? " / night" : " / notte"), 12, INK, Font.BOLD);
        bottom.add(price, BorderLayout.EAST);
        details.add(Box.createVerticalStrut(12));
        details.add(bottom);
        card.add(details, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSearchPanel() {
        SurfacePanel panel = new SurfacePanel(Color.WHITE, LINE, 12);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(18, 20, 17, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(fieldBlock(english ? "APARTMENT" : "APPARTAMENTO", apartmentChoice = new JComboBox<>(new String[] {"Brera Loft", "Navigli Studio"})), c);
        c.gridx = 1;
        panel.add(fieldBlock("CHECK-IN", checkInField = new JTextField(LocalDate.now().plusDays(14).format(DATE_FORMAT))), c);
        c.gridx = 2;
        panel.add(fieldBlock("CHECK-OUT", checkOutField = new JTextField(LocalDate.now().plusDays(17).format(DATE_FORMAT))), c);
        c.gridx = 3;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(fieldBlock(english ? "GUESTS" : "OSPITI", guestCount = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1))), c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.insets = new Insets(15, 0, 0, 0);
        JPanel actionLine = new JPanel(new BorderLayout(12, 0));
        actionLine.setOpaque(false);
        validationLabel = label(" ", 12, ACCENT, Font.PLAIN);
        actionLine.add(validationLabel, BorderLayout.CENTER);
        calculateButton = primaryButton(english ? "Calculate estimate   →" : "Calcola il preventivo   →");
        calculateButton.addActionListener(event -> calculateQuote());
        actionLine.add(calculateButton, BorderLayout.EAST);
        panel.add(actionLine, c);
        return panel;
    }

    private JPanel fieldBlock(String title, java.awt.Component input) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        JLabel caption = label(title, 10, MUTED, Font.BOLD);
        caption.setBorder(new EmptyBorder(0, 0, 6, 0));
        block.add(caption);
        input.setFont(new Font("SansSerif", Font.PLAIN, 14));
        input.setForeground(INK);
        if (input instanceof JTextField) {
            ((JTextField) input).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE), new EmptyBorder(8, 9, 8, 9)));
        } else if (input instanceof JComboBox) {
            ((JComboBox<?>) input).setBackground(Color.WHITE);
            ((JComboBox<?>) input).setBorder(BorderFactory.createLineBorder(LINE));
        } else if (input instanceof JSpinner) {
            ((JSpinner) input).setBorder(BorderFactory.createLineBorder(LINE));
        }
        input.setPreferredSize(new Dimension(120, 39));
        block.add(input);
        return block;
    }

    private JPanel buildQuoteAndRequest() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(LEFT_ALIGNMENT);

        SurfacePanel quotePanel = new SurfacePanel(new Color(237, 241, 233), new Color(218, 225, 213), 12);
        quotePanel.setLayout(new BorderLayout(12, 0));
        quotePanel.setBorder(new EmptyBorder(17, 20, 17, 20));
        JPanel quoteCopy = new JPanel();
        quoteCopy.setOpaque(false);
        quoteCopy.setLayout(new BoxLayout(quoteCopy, BoxLayout.Y_AXIS));
        quoteCopy.add(label(english ? "YOUR STAY" : "IL TUO SOGGIORNO", 10, GREEN, Font.BOLD));
        quoteDetails = label("", 14, INK, Font.PLAIN);
        quoteDetails.setBorder(new EmptyBorder(7, 0, 0, 0));
        quoteCopy.add(quoteDetails);
        quotePanel.add(quoteCopy, BorderLayout.CENTER);
        quotePrice = label("", 24, GREEN_DARK, Font.BOLD, "Georgia");
        quotePrice.setHorizontalAlignment(SwingConstants.RIGHT);
        quotePanel.add(quotePrice, BorderLayout.EAST);
        section.add(quotePanel);
        JLabel disclaimer = label(english
            ? "Demo estimate. Availability is not checked in real time; taxes and extras are not included."
            : "Stima demo: disponibilità non verificata in tempo reale. Imposte ed extra non inclusi.", 11, MUTED, Font.PLAIN);
        disclaimer.setBorder(new EmptyBorder(7, 4, 14, 0));
        disclaimer.setAlignmentX(LEFT_ALIGNMENT);
        section.add(disclaimer);

        requestSection = new JPanel(new GridBagLayout());
        requestSection.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 10, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        requestSection.add(fieldBlock(english ? "FULL NAME" : "NOME E COGNOME", nameField = new JTextField()), c);
        c.gridx = 1;
        requestSection.add(fieldBlock("EMAIL", emailField = new JTextField()), c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        JTextArea note = new JTextArea(2, 20);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setFont(new Font("SansSerif", Font.PLAIN, 13));
        note.setForeground(INK);
        note.setBorder(new EmptyBorder(8, 9, 8, 9));
        noteField = note;
        requestSection.add(fieldBlock(english ? "NOTE TO THE HOST" : "NOTA PER IL PROPRIETARIO", note), c);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(1, 0, 0, 0);
        JPanel submitLine = new JPanel(new BorderLayout(10, 0));
        submitLine.setOpaque(false);
        requestFeedbackLabel = label(" ", 12, ACCENT, Font.PLAIN);
        submitLine.add(requestFeedbackLabel, BorderLayout.CENTER);
        requestButton = primaryButton(english ? "Send demo request   →" : "Invia richiesta demo   →");
        requestButton.addActionListener(event -> submitRequest());
        submitLine.add(requestButton, BorderLayout.EAST);
        requestSection.add(submitLine, c);
        section.add(requestSection);

        JPanel finalNotice = new JPanel(new BorderLayout(8, 0));
        finalNotice.setOpaque(false);
        confirmationLabel = label(" ", 12, GREEN, Font.BOLD);
        JLabel notice = label(english
            ? "This demo request is not sent or saved. Connect a booking service to accept real reservations."
            : "La richiesta demo non viene inviata né salvata. Per prenotazioni reali serve un servizio collegato.", 11, MUTED, Font.PLAIN);
        notice.setBorder(new EmptyBorder(9, 0, 0, 0));
        JPanel noticeCopy = new JPanel();
        noticeCopy.setOpaque(false);
        noticeCopy.setLayout(new BoxLayout(noticeCopy, BoxLayout.Y_AXIS));
        noticeCopy.add(confirmationLabel);
        noticeCopy.add(notice);
        finalNotice.add(noticeCopy, BorderLayout.CENTER);
        newSearchButton = new JButton(english ? "Start a new search" : "Inizia una nuova ricerca");
        newSearchButton.setForeground(GREEN);
        newSearchButton.setContentAreaFilled(false);
        newSearchButton.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 2));
        newSearchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newSearchButton.addActionListener(event -> resetSearch());
        finalNotice.add(newSearchButton, BorderLayout.EAST);
        section.add(finalNotice);

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { clearConfirmation(); }
            public void removeUpdate(DocumentEvent event) { clearConfirmation(); }
            public void changedUpdate(DocumentEvent event) { clearConfirmation(); }
        });
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { clearConfirmation(); }
            public void removeUpdate(DocumentEvent event) { clearConfirmation(); }
            public void changedUpdate(DocumentEvent event) { clearConfirmation(); }
        });
        return section;
    }

    private void calculateQuote() {
        validationLabel.setText(" ");
        Apartment apartment = APARTMENTS[apartmentChoice.getSelectedIndex()];
        LocalDate arrival;
        LocalDate departure;
        try {
            arrival = LocalDate.parse(checkInField.getText().trim(), DATE_FORMAT);
            departure = LocalDate.parse(checkOutField.getText().trim(), DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            validationLabel.setText(english ? "Enter dates in DD/MM/YYYY format." : "Inserisci le date nel formato GG/MM/AAAA.");
            return;
        }
        if (!arrival.isAfter(LocalDate.now())) {
            validationLabel.setText(english ? "Check-in must be a future date." : "Il check-in deve essere una data futura.");
            return;
        }
        if (!departure.isAfter(arrival)) {
            validationLabel.setText(english ? "Check-out must be after check-in." : "Il check-out deve essere successivo al check-in.");
            return;
        }
        int guests = (Integer) guestCount.getValue();
        if (guests > apartment.capacity) {
            validationLabel.setText(english
                ? apartment.name + " accommodates up to " + apartment.capacity + " guests."
                : apartment.name + " può ospitare al massimo " + apartment.capacity + " persone.");
            return;
        }
        currentQuote = new Quote(apartment, arrival, departure, guests);
        requestSent = false;
        requestCode = null;
        nameField = null;
        emailField = null;
        noteField = null;
        render();
        updateQuoteLabels();
    }

    private void updateQuoteLabels() {
        if (currentQuote == null || quoteDetails == null) {
            return;
        }
        Quote q = currentQuote;
        long nights = q.nights();
        String guests = q.guests == 1 ? (english ? "1 guest" : "1 ospite")
            : q.guests + (english ? " guests" : " ospiti");
        String nightText = nights == 1 ? (english ? "1 night" : "1 notte")
            : nights + (english ? " nights" : " notti");
        quoteDetails.setText("<html><b>" + q.apartment.name + "</b>  ·  " + guests + "<br>"
            + q.arrival.format(DATE_FORMAT) + "  —  " + q.departure.format(DATE_FORMAT)
            + "  ·  " + nightText + "  ·  " + euro(q.apartment.rate) + (english ? " / night" : " / notte") + "</html>");
        quotePrice.setText(euro(q.total()));
        confirmationLabel.setText(requestSent
            ? (english ? "Demo request recorded  ·  reference " : "Richiesta demo registrata  ·  codice ") + requestCode
            : " ");
        requestSection.setVisible(!requestSent);
        newSearchButton.setVisible(requestSent);
    }

    private void submitRequest() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty()) {
            showValidation(english ? "Enter your full name." : "Inserisci nome e cognome.");
            nameField.requestFocusInWindow();
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showValidation(english ? "Enter a valid email address." : "Inserisci un indirizzo email valido.");
            emailField.requestFocusInWindow();
            return;
        }
        requestCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
        requestSent = true;
        updateQuoteLabels();
        revalidate();
        repaint();
    }

    private void showValidation(String message) {
        requestFeedbackLabel.setText(message);
    }

    private void clearConfirmation() {
        if (requestFeedbackLabel != null && !requestSent) {
            requestFeedbackLabel.setText(" ");
        }
    }

    private void resetSearch() {
        currentQuote = null;
        requestCode = null;
        requestSent = false;
        apartmentChoice = null;
        checkInField = null;
        checkOutField = null;
        guestCount = null;
        nameField = null;
        emailField = null;
        noteField = null;
        render();
    }

    private String euro(int amount) {
        NumberFormat format = NumberFormat.getIntegerInstance(english ? Locale.UK : Locale.ITALY);
        return "€ " + format.format(amount);
    }

    private JPanel sectionHeading(String title, String subtitle) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(LEFT_ALIGNMENT);
        JLabel heading = label(title, 22, INK, Font.BOLD, "Georgia");
        heading.setAlignmentX(LEFT_ALIGNMENT);
        JLabel sub = label(subtitle, 12, MUTED, Font.PLAIN);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        block.add(heading);
        block.add(Box.createVerticalStrut(4));
        block.add(sub);
        return block;
    }

    private JLabel label(String text, int size, Color color, int style) {
        return label(text, size, color, style, "SansSerif");
    }

    private JLabel label(String text, int size, Color color, int style, String family) {
        JLabel result = new JLabel(text);
        result.setFont(new Font(family, style, size));
        result.setForeground(color);
        return result;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(GREEN);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static class Apartment {
        final String name;
        final String areaIt;
        final String areaEn;
        final int capacity;
        final int rate;
        final String descriptionIt;
        final String descriptionEn;
        final String photoUrl;

        Apartment(String name, String areaIt, String areaEn, int capacity, int rate,
                  String descriptionIt, String descriptionEn, String photoUrl) {
            this.name = name;
            this.areaIt = areaIt;
            this.areaEn = areaEn;
            this.capacity = capacity;
            this.rate = rate;
            this.descriptionIt = descriptionIt;
            this.descriptionEn = descriptionEn;
            this.photoUrl = photoUrl;
        }
    }

    private static class Quote {
        final Apartment apartment;
        final LocalDate arrival;
        final LocalDate departure;
        final int guests;

        Quote(Apartment apartment, LocalDate arrival, LocalDate departure, int guests) {
            this.apartment = apartment;
            this.arrival = arrival;
            this.departure = departure;
            this.guests = guests;
        }

        long nights() {
            return java.time.temporal.ChronoUnit.DAYS.between(arrival, departure);
        }

        long total() {
            return nights() * apartment.rate;
        }
    }

    private static class SurfacePanel extends JPanel {
        private final Color fill;
        private final Color stroke;
        private final int radius;

        SurfacePanel(Color fill, Color stroke, int radius) {
            this.fill = fill;
            this.stroke = stroke;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.setColor(stroke);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class HeroPanel extends JPanel {
        HeroPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, GREEN_DARK, getWidth(), getHeight(), GREEN));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g.setColor(new Color(255, 255, 255, 20));
            for (int i = 0; i < 5; i++) {
                g.drawOval(getWidth() - 230 + i * 20, -110 + i * 18, 220, 220);
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class PhotoPanel extends JPanel {
        private BufferedImage image;
        private final boolean brera;

        PhotoPanel(String url, boolean brera) {
            this.brera = brera;
            setOpaque(false);
            new SwingWorker<BufferedImage, Void>() {
                @Override
                protected BufferedImage doInBackground() throws Exception {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setConnectTimeout(2500);
                    connection.setReadTimeout(3500);
                    return ImageIO.read(connection.getInputStream());
                }

                @Override
                protected void done() {
                    try {
                        image = get();
                    } catch (Exception ignored) {
                        image = null;
                    }
                    repaint();
                }
            }.execute();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (image == null) {
                Color first = brera ? new Color(194, 174, 146) : new Color(157, 181, 169);
                Color second = brera ? new Color(111, 127, 112) : new Color(75, 105, 95);
                g.setPaint(new GradientPaint(0, 0, first, getWidth(), getHeight(), second));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g.setColor(new Color(255, 255, 255, 55));
                for (int i = 0; i < 6; i++) {
                    int x = 24 + i * 67;
                    g.fillRoundRect(x, 38 + (i % 2) * 10, 36, 61, 3, 3);
                }
                g.setColor(new Color(255, 255, 255, 35));
                g.fillRect(0, getHeight() - 26, getWidth(), 26);
            } else {
                double scale = Math.max((double) getWidth() / image.getWidth(), (double) getHeight() / image.getHeight());
                int width = (int) Math.ceil(image.getWidth() * scale);
                int height = (int) Math.ceil(image.getHeight() * scale);
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;
                g.clip(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 9, 9));
                g.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), x, y, null);
                g.setColor(new Color(19, 34, 27, 45));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PrenotaMilano().setVisible(true));
    }
}