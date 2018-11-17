
package utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.common.collect.ImmutableList;

import java.io.*;
import java.util.Collection;
import java.util.List;

// Crawls mydrive/xforce (1LEALeVfu43XjriVzK1gMCJ8hOkJBYEsa) folder, dumps the photo content.
public class Crawler {
    private static final String APPLICATION_NAME = "XForce Client";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String CREDENTIALS_FOLDER = USER_HOME + "/Downloads/";
//    private static final String CREDENTIALS_FOLDER = "/Users/govardhanreddy/Downloads/";
    private static final String CLIENT_SECRET = "client_secret_google.json";
    // receipts folder
    private static final String PARENT_FOLDER_DRIVE = "1LEALeVfu43XjriVzK1gMCJ8hOkJBYEsa"; //

    private static final Collection<String> SCOPES = ImmutableList.of(SheetsScopes.DRIVE,
            SheetsScopes.DRIVE_FILE, SheetsScopes.SPREADSHEETS, DriveScopes.DRIVE, DriveScopes
                    .DRIVE_READONLY);

    private static final class ImageMetadata {
        final String date;
        final String amount;
        final String url;

        public ImageMetadata(String date, String amount, String url) {
            this.date = date;
            this.amount = amount;
            this.url = url;
        }
    }

    public static void main(String args[]) throws Exception {

        Drive driveService = createDriveService();
        Sheets sheetsService = createSheetsService();


        FileList files = getFilesInFolder(driveService, PARENT_FOLDER_DRIVE);
        for (com.google.api.services.drive.model.File file : files.getFiles()) {
            System.out.println("### name: " + file.getName() + " date: " + " id: " + file.getId()
                    + file.getCreatedTime());
            download(driveService, file.getId(), file.getName());
        }
    }

    private static void download(Drive service, String id, String name) throws Exception {
        File file = new File(CREDENTIALS_FOLDER + name);
        FileOutputStream fos = new FileOutputStream(file);
        if (!file.exists()) {
            file.createNewFile();
        }
        service.files().get(id).executeMediaAndDownloadTo(fos);
        fos.flush();
        fos.close();
        System.out.println("Downloaded ... ");
    }


    private static FileList getFilesInFolder(Drive service, String folderId) throws IOException {
        Drive.Files.List request;
        request = service.files().list();
        String query = "'" + folderId + "' in parents";
        request = request.setQ(query);
        FileList files = request.execute();
        System.out.println("### folder size: " + files.getFiles().size());
        return files;
    }

    private static Drive createDriveService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials
                (HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();
        return service;
    }

    private static String getUrl(ImageMetadata imageMetadata, List<ImageMetadata> images) {
        for (ImageMetadata imageMetadata1 : images) {
            if (imageMetadata1.date.equals(imageMetadata.date) && imageMetadata1.amount.equals
                    (imageMetadata.amount)) {
                return imageMetadata1.url;
            }
        }
        return null;
    }

    public static Sheets createSheetsService() throws Exception {
        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Credential credential = getCredentials(httpTransport);
        System.out.println(" access token: " + credential.getAccessToken());
        System.out.println(" refresh token: " + credential.getRefreshToken());

        return new Sheets.Builder(httpTransport, jsonFactory, credential).setApplicationName
                ("Google-SheetsSample/0.1").build();
    }

    private static Credential getCredentials1(final NetHttpTransport HTTP_TRANSPORT) throws
            Exception {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(new File
                (CREDENTIALS_FOLDER + CLIENT_SECRET)));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, isr);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder

                (HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(new
                FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setTokenServerUrl(
                        new GenericUrl("https://test-www.sandbox.googleapis.com/oauth2/v4/token"))
                .setAuthorizationServerEncodedUrl("https://gaiastaging.corp.google.com/o/oauth2/auth")
                .setAccessType
                        ("offline").build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        //        return getServiceAccountCredentials(HTTP_TRANSPORT);
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(new File(CREDENTIALS_FOLDER + CLIENT_SECRET)));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, isr);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}


