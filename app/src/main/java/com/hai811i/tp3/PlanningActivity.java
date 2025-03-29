package com.hai811i.tp3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlanningActivity extends AppCompatActivity implements EventAdapter.OnEventDeleteListener, EventAdapter.OnEventEditListener {

    private TextView textViewWelcome, textViewUserInfo;
    private RecyclerView horizontalCalendar;
    private ListView eventListView;
    private Button addEventButton, toggleEventsButton;
    private Map<String, List<Event>> eventsMap;
    private EventAdapter eventAdapter;
    private String currentDate;
    private DatabaseHelper dbHelper;
    private boolean areEventsVisible = false;
    private String[] colorNames = {"Rouge", "Vert", "Bleu", "Jaune", "Orange", "Violet"};
    private int[] colorValues = {0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFFA500, 0xFF800080};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);


        initializeViews();


        displayUserInfo();


        dbHelper = new DatabaseHelper(this);
        eventsMap = new HashMap<>();
        loadEventsFromDatabase();


        eventAdapter = new EventAdapter(this, new ArrayList<>(), this, this);
        eventListView.setAdapter(eventAdapter);
        eventListView.setVisibility(View.GONE);


        setupButtons();


        setupHorizontalCalendar();
    }

    private void initializeViews() {
        textViewWelcome = findViewById(R.id.textViewWelcome);
        textViewUserInfo = findViewById(R.id.textViewUserInfo);
        horizontalCalendar = findViewById(R.id.horizontalCalendar);
        eventListView = findViewById(R.id.eventListView);
        addEventButton = findViewById(R.id.addEventButton);
        toggleEventsButton = findViewById(R.id.toggleEventsButton);
    }

    private void displayUserInfo() {
        Intent intent = getIntent();
        String userLogin = intent.getStringExtra("login");
        String userNom = intent.getStringExtra("nom");
        String userPrenom = intent.getStringExtra("prenom");

        if (userLogin != null && userNom != null && userPrenom != null) {
            textViewWelcome.setText("Bienvenue, " + userPrenom + " !");
            textViewUserInfo.setText("Nom: " + userNom + "\nPrénom: " + userPrenom + "\nLogin: " + userLogin);
        } else {
            textViewWelcome.setText("Bienvenue !");
            textViewUserInfo.setText("Informations de l'utilisateur non disponibles.");
        }
    }

    private void setupButtons() {
        toggleEventsButton.setOnClickListener(v -> {
            areEventsVisible = !areEventsVisible;
            eventListView.setVisibility(areEventsVisible ? View.VISIBLE : View.GONE);
            toggleEventsButton.setText(areEventsVisible ?
                    "Masquer les événements" : "Afficher les événements");
            if (areEventsVisible) {
                updateEventList(currentDate);
            }
        });

        addEventButton.setOnClickListener(v -> showAddEventDialog());
    }

    private void setupHorizontalCalendar() {
        List<Date> dates = generateDates();
        HorizontalCalendarAdapter adapter = new HorizontalCalendarAdapter(this, dates, selectedDate -> {
            currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate);
            if (areEventsVisible) {
                updateEventList(currentDate);
            }
        });

        horizontalCalendar.setAdapter(adapter);
        horizontalCalendar.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );


        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(today);
        adapter.selectDate(today);
        horizontalCalendar.scrollToPosition(adapter.getSelectedPosition());
    }

    private List<Date> generateDates() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = -15; i <= 15; i++) {
            Calendar temp = (Calendar) calendar.clone();
            temp.add(Calendar.DAY_OF_MONTH, i);
            dates.add(temp.getTime());
        }
        return dates;
    }

    @SuppressLint("Range")
    private void loadEventsFromDatabase() {
        eventsMap.clear();
        String login = getIntent().getStringExtra("login");

        if (login != null) {
            Cursor cursor = dbHelper.getEventsByUser(login);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                    String h8h10h = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_8H_10H));
                    String h10h12h = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_10H_12H));
                    String h14h16h = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_14H_16H));
                    String h16h18h = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_16H_18H));

                    if (h8h10h != null && !h8h10h.isEmpty()) {
                        addEventToMap(date, new Event(h8h10h, "08h-10h", getColorForEvent(h8h10h)));
                    }
                    if (h10h12h != null && !h10h12h.isEmpty()) {
                        addEventToMap(date, new Event(h10h12h, "10h-12h", getColorForEvent(h10h12h)));
                    }
                    if (h14h16h != null && !h14h16h.isEmpty()) {
                        addEventToMap(date, new Event(h14h16h, "14h-16h", getColorForEvent(h14h16h)));
                    }
                    if (h16h18h != null && !h16h18h.isEmpty()) {
                        addEventToMap(date, new Event(h16h18h, "16h-18h", getColorForEvent(h16h18h)));
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
    }

    private void addEventToMap(String date, Event event) {
        if (!eventsMap.containsKey(date)) {
            eventsMap.put(date, new ArrayList<>());
        }
        eventsMap.get(date).add(event);
    }

    private Map<String, Integer> colorCache = new HashMap<>();

    private int getColorForEvent(String eventTitle) {
        if (colorCache.containsKey(eventTitle)) {
            return colorCache.get(eventTitle);
        }

        int hash = eventTitle.hashCode();
        int color = colorValues[Math.abs(hash) % colorValues.length];
        colorCache.put(eventTitle, color);
        return color;
    }




    public void onEventDelete(Event event) {
        ContentValues values = new ContentValues();
        String login = getIntent().getStringExtra("login");
        if (login != null) {

            switch (event.getTime()) {
                case "08h-10h":
                    values.put(DatabaseHelper.COLUMN_8H_10H, "");
                    break;
                case "10h-12h":
                    values.put(DatabaseHelper.COLUMN_10H_12H, "");
                    break;
                case "14h-16h":
                    values.put(DatabaseHelper.COLUMN_14H_16H, "");
                    break;
                case "16h-18h":
                    values.put(DatabaseHelper.COLUMN_16H_18H, "");
                    break;
            }

            boolean success = dbHelper.deleteEventForUser(login, currentDate, event.getTime());
            if (success) {
                loadEventsFromDatabase();
                updateEventList(currentDate);
                Toast.makeText(this, "Événement supprimé", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateEventList(String date) {
        if (!areEventsVisible) return;

        sortEventsByTime(date);

        List<Event> events = eventsMap.get(date);
        eventAdapter.clear();
        if (events != null) {
            eventAdapter.addAll(events);
        }
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventEdit(Event event) {
        showEditEventDialog(event);
    }

    private void showAddEventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.eventTitleInput);
        Spinner eventTimeSpinner = dialogView.findViewById(R.id.eventTimeSpinner);
        Spinner colorPicker = dialogView.findViewById(R.id.eventColorSpinner);
        Button saveEventButton = dialogView.findViewById(R.id.saveEventButton);

        final AlertDialog dialog = builder.create();
        final String[] selectedTime = {""};

        setupColorSpinner(colorPicker);

        String[] timeSlots = getResources().getStringArray(R.array.time_slots);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTimeSpinner.setAdapter(adapter);

        eventTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTime[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        saveEventButton.setOnClickListener(v -> {
            String title = eventTitleInput.getText().toString();
            String login = getIntent().getStringExtra("login");
            int selectedColor = colorValues[colorPicker.getSelectedItemPosition()];

            if (!title.isEmpty() && !selectedTime[0].isEmpty() && login != null) {
                ContentValues values = new ContentValues();
                switch (selectedTime[0]) {
                    case "08h-10h": values.put(DatabaseHelper.COLUMN_8H_10H, title); break;
                    case "10h-12h": values.put(DatabaseHelper.COLUMN_10H_12H, title); break;
                    case "14h-16h": values.put(DatabaseHelper.COLUMN_14H_16H, title); break;
                    case "16h-18h": values.put(DatabaseHelper.COLUMN_16H_18H, title); break;
                }

                long result = dbHelper.addOrUpdateEventForUser(login, currentDate, values);

                if (result != -1) {
                    loadEventsFromDatabase();
                    updateEventList(currentDate);
                    Toast.makeText(PlanningActivity.this, "Événement ajouté", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(PlanningActivity.this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PlanningActivity.this,
                        "Veuillez entrer un titre et sélectionner une heure",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showEditEventDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_event_dialog, null);
        builder.setView(dialogView);

        EditText eventTitleInput = dialogView.findViewById(R.id.eventTitleInput);
        Spinner timePickerSpinner = dialogView.findViewById(R.id.eventTimeSpinner);
        Spinner colorPicker = dialogView.findViewById(R.id.eventColorSpinner);
        Button saveEventButton = dialogView.findViewById(R.id.saveEventButton);

        eventTitleInput.setText(event.getTitle());

        String[] timeSlots = {"08h-10h", "10h-12h", "14h-16h", "16h-18h"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSlots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePickerSpinner.setAdapter(adapter);

        int index = Arrays.asList(timeSlots).indexOf(event.getTime());
        if (index != -1) {
            timePickerSpinner.setSelection(index);
        }

        setupColorSpinner(colorPicker);

        int selectedColorIndex = -1;
        for (int i = 0; i < colorValues.length; i++) {
            if (colorValues[i] == event.getColor()) {
                selectedColorIndex = i;
                break;
            }
        }
        colorPicker.setSelection(selectedColorIndex);

        final AlertDialog dialog = builder.create();

        saveEventButton.setOnClickListener(v -> {
            String title = eventTitleInput.getText().toString();
            String login = getIntent().getStringExtra("login");
            int selectedColor = colorValues[colorPicker.getSelectedItemPosition()];
            String selectedTime = timePickerSpinner.getSelectedItem().toString();

            if (!title.isEmpty() && !selectedTime.isEmpty()) {
                // Supprimer l'ancien événement
                ContentValues clearValues = new ContentValues();
                switch (event.getTime()) {
                    case "08h-10h": clearValues.put(DatabaseHelper.COLUMN_8H_10H, ""); break;
                    case "10h-12h": clearValues.put(DatabaseHelper.COLUMN_10H_12H, ""); break;
                    case "14h-16h": clearValues.put(DatabaseHelper.COLUMN_14H_16H, ""); break;
                    case "16h-18h": clearValues.put(DatabaseHelper.COLUMN_16H_18H, ""); break;
                }
                dbHelper.updateEventForUserAndDate(login, currentDate, clearValues);

                // Ajouter le nouvel événement
                ContentValues values = new ContentValues();
                switch (selectedTime) {
                    case "08h-10h": values.put(DatabaseHelper.COLUMN_8H_10H, title); break;
                    case "10h-12h": values.put(DatabaseHelper.COLUMN_10H_12H, title); break;
                    case "14h-16h": values.put(DatabaseHelper.COLUMN_14H_16H, title); break;
                    case "16h-18h": values.put(DatabaseHelper.COLUMN_16H_18H, title); break;
                }

                dbHelper.addOrUpdateEventForUser(login, currentDate, values);
                loadEventsFromDatabase();
                updateEventList(currentDate);
                Toast.makeText(PlanningActivity.this, "Événement modifié", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(PlanningActivity.this,
                        "Veuillez entrer un titre et sélectionner une heure",
                        Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void setupColorSpinner(Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                colorNames);
        spinner.setAdapter(adapter);
    }

    private void sortEventsByTime(String date) {
        List<Event> events = eventsMap.get(date);
        if (events != null) {
            events.sort((e1, e2) -> e1.getTime().compareTo(e2.getTime()));
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}