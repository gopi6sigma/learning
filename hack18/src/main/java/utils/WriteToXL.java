package utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
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
import com.google.api.services.sheets.v4.model.*;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WriteToXL {
    private static final String APPLICATION_NAME = "XForce Client";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "/Users/govardhanreddy/Downloads/";
    // receipts folder
    private static final String PARENT_FOLDER_DRIVE = "1F8DIkFgVe7lH_vQz_nsVWhp2_IYqbQe8"; //


    /*


    https://gaiastaging.corp.google.com/signin/oauth/oauthchooseaccount?client_id=109862544572002312937&as=vBxPex8ioQXXsjLC5I2XCg&destination=http%3A%2F%2Flocalhost%3A8085&approval_state=!ChQxRE0zQTlHUGlKTGduVnQzUnlTYhIfczl2dmFRa2NPZElkUUMxOE9XTml0YmF0UjRzdVBSWQ%E2%88%99APFTVPcAAAAAWxiXUOj4RMKcaOI9v5fLAOGXE5cSZ5QY&xsrfsig=AACX0OzFpL50qblh04upo6wEayjgUe8Tbw&flowName=GeneralOAuthFlow


    https://gaiastaging.corp.google.com/signin/oauth/consent?authuser=0&part=AJi8hAMBv_QGOerABuKlGbaiLbyTBn5k5_Fm4hMVS-YZot8AIFqbN6hpZkeoGa8n4U8n2G3PJYLqWn32HDtom3ngeVbossnrVoqg0-LKJQHFKfLM-DXsvcZAh7jUtJdaVlxNn8syFQi52jJHsOEIOs5ebc-rClXky9RWrFXIR0eN59ddqtSg2PJ8C2DvzoAKq4Iz2jL0lFREOGgMg2RxPolG2p9VJZImtaHnMHjNE0fQTyNdCzZYzd7nBHrDfTKE3thIBqzqJA_N2RoduLJ0mHhoN_Xn_oYrD2ro42V7UNSZMzonbGe9lLaSxMDpsjDwq5VQ27m773D_1oW-SPTcmKDGds-ao33nU4vUj0TzMS9l0OPUuKqMXhu3KCFjTNcpzz9_J0XK-HHHDJ9NbP-sZ2mN6YL-KObAODPx9pNTl4SkVOTxy9wSgy4ty2dAuflUu8TtVvMV5VloI1cmDbZIH5BMbQxcgyaAt-HdBv_1CF149ZUzoqVHOT1SNRN6dOXOyAkrYCi2QgVTKnlCU_zhY5uL6XkXJQSW03wskrjiC_ISd7zAe0fNj5-Kw9mnhxoEEFBhBWdppB3asE1DfGtkTy852qVrJcefuWwAjFMdZ5flgkn6rU9QGEl06gVwh6FyOycjOr6uENfdDJdsqmIfGP1AqCJN1g6_5Q&hl=en&as=KZvAvlSA8Qw0y0Y2VXN91w#


    https://accounts.google.com/signin/oauth/consent?authuser=0&part=AJi8hAMBv_QGOerABuKlGbaiLbyTBn5k5_Fm4hMVS-YZot8AIFqbN6hpZkeoGa8n4U8n2G3PJYLqWn32HDtom3ngeVbossnrVoqg0-LKJQHFKfLM-DXsvcZAh7jUtJdaVlxNn8syFQi52jJHsOEIOs5ebc-rClXky9RWrFXIR0eN59ddqtSg2PJ8C2DvzoAKq4Iz2jL0lFREOGgMg2RxPolG2p9VJZImtaHnMHjNE0fQTyNdCzZYzd7nBHrDfTKE3thIBqzqJA_N2RoduLJ0mHhoN_Xn_oYrD2ro42V7UNSZMzonbGe9lLaSxMDpsjDwq5VQ27m773D_1oW-SPTcmKDGds-ao33nU4vUj0TzMS9l0OPUuKqMXhu3KCFjTNcpzz9_J0XK-HHHDJ9NbP-sZ2mN6YL-KObAODPx9pNTl4SkVOTxy9wSgy4ty2dAuflUu8TtVvMV5VloI1cmDbZIH5BMbQxcgyaAt-HdBv_1CF149ZUzoqVHOT1SNRN6dOXOyAkrYCi2QgVTKnlCU_zhY5uL6XkXJQSW03wskrjiC_ISd7zAe0fNj5-Kw9mnhxoEEFBhBWdppB3asE1DfGtkTy852qVrJcefuWwAjFMdZ5flgkn6rU9QGEl06gVwh6FyOycjOr6uENfdDJdsqmIfGP1AqCJN1g6_5Q&hl=en&as=KZvAvlSA8Qw0y0Y2VXN91w#


     */


    // my drive
    //https://docs.google.com/spreadsheets/d/1b5yX3xBj-Sis8Q43jnrC_P9PkWXi
    // -pEaaHm0jA4RtTk/edit?usp=sharing
    private static final String SPREAD_SHEET_ID = "1b5yX3xBj-Sis8Q43jnrC_P9PkWXi-pEaaHm0jA4RtTk";
    private static final String CLIENT_SECRET = "client_secret_google.json";

    // spread sheet in admin@stanton
    // https://docs-qa.corp.google.com/spreadsheets/d/17rPlTGckiufw6ZUgRnvZVtyaIfda2WwSmTAXK3XSOnw
//    private static final String SPREAD_SHEET_ID = "17rPlTGckiufw6ZUgRnvZVtyaIfda2WwSmTAXK3XSOnw";
//    private static final String CLIENT_SECRET = "client_secret_stanton_test.json";
//    private static final String SERVICE_ACCOUNT_KEY_P12 = "xforce-sa-key.p12";
    private static final String SERVICE_ACCOUNT_KEY_JSON = "xforce-sa-key.json";
    private static final String EMAIL_ADDRESS = "admin@stantonj.1bo2.info";

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

        //        String inputFile = fileToString(args[0]);
        String inputFile = fileToString("/Users/govardhanreddy/Downloads/images_input.json");

        Gson gson = new Gson();
        Type imType = new TypeToken<ArrayList<ImageMetadata>>() {
        }.getType();
        List<ImageMetadata> inputImages = gson.fromJson(inputFile, imType);

        Drive driveService = createDriveService();

        Sheets sheetsService = createSheetsService();

        printData(sheetsService);

//        List<ImageMetadata> extracted = extractExpenses(sheetsService);
//        update(sheetsService, extracted, inputImages);
//        append(sheetsService, extracted, inputImages);
//        //
//        createFolder(extracted);
//        System.out.println("### input length: " + inputImages.size() + " " + "length from sheet: " +
//                "" + extracted.size() + " a - b length: " + getAMinusB(inputImages, extracted)
//                .size());
//        createFolder(getAMinusB(inputImages, extracted));
//        copyReceipts(driveService, inputImages);
    }

    private static void copyReceipts(Drive service, List<ImageMetadata> receipts) throws Exception {
        for (ImageMetadata receipt : receipts) {
            String yearId = getExistsFolder(service, getNormalizedYear(receipt.date),
                    PARENT_FOLDER_DRIVE);
            String monthId = getExistsFolder(service, getNormalizedMonth(receipt.date), yearId);
            copyFile(service, receipt.url, monthId);
        }
    }


    private static void copyFile(Drive service, String docId, String parentId) throws Exception {
        try {
            service.files().copy(docId, new com.google.api.services.drive.model.File().setParents(ImmutableList.of(parentId))).execute();
        } catch(Exception ex) {
            System.out.println("### Apparently file not found. DocId: " + docId);
            System.out.println("Exception: " + ex);
        }
    }

    private static void append(Sheets service, List<ImageMetadata> parsed, List<ImageMetadata>
            inputImages) throws Exception {
        System.out.println("### parsed size : " + parsed.size());
        List<ImageMetadata> rowsToAppend = getAMinusB(inputImages, parsed);


        for (int i = 0; i < rowsToAppend.size(); i++) {
            int rowNum = parsed.size() + i + 1;
            ImageMetadata image = rowsToAppend.get(i);
            Sheets.Spreadsheets.Values.Append request = service.spreadsheets().values().append
                    (SPREAD_SHEET_ID, "A" + rowNum + ":D" + rowNum, new ValueRange().setValues(
                            (ImmutableList) ImmutableList.of(ImmutableList.of(image.date,
                                    "Miscellaneous", image.amount, "https://drive.google.com/open?id=" + image.url))));
            request.setValueInputOption("RAW");

            AppendValuesResponse response = request.execute();
            System.out.println("### Append resp: " + response);
        }

    }

    private static List<ImageMetadata> getAMinusB(List<ImageMetadata> input, List<ImageMetadata>
            parsed) {
        List<ImageMetadata> output = new ArrayList<ImageMetadata>();
        for (ImageMetadata image : input) {
            if (contains(image, parsed)) {
                continue;
            }

            output.add(image);
        }
        return output;
    }

    private static boolean contains(ImageMetadata image, List<ImageMetadata> list) {
        for (ImageMetadata i : list) {
            if (image.date.equals(i.date) && image.amount.equals(i.amount)) {
                return true;
            }
        }
        return false;
    }

    private static void update(Sheets service, List<ImageMetadata> extracted, List<ImageMetadata>
            inputImages) throws Exception {
        for (int i = 0; i < extracted.size(); i++) {
            String url = getUrl(extracted.get(i), inputImages);
            if (null == url) {
                continue;
            }
            url = "https://drive.google.com/open?id=" + url;
            List<List<Object>> values = new ArrayList<List<Object>>();
            List<Object> value = new ArrayList<Object>();

//            value.add(new CellData().setUserEnteredValue(new ExtendedValue().setFormulaValue
//                    ("=HYPERLINK(\"" + url + ",\"SO link\")")));
//            value.add("https://drive.google.com/open?id=" + url);
            String quote = "" + '"';

            String blah = quote + "http://stackoverflow.com" + quote
                    + "," + quote + "SO label" + quote;


//            String b = "=HYPERLINK(\"drive.google.com\",\"blah\")";
            String b = "=HYPERLINK(" + blah + ")";
            System.out.println(" **** #### Final b: " + b);
//            String b = "=HYPERLINK('drive.google.com','blah')";
            value.add(new CellData().setUserEnteredValue(new ExtendedValue().setFormulaValue
                    (b)));
            values.add(value);

            Sheets.Spreadsheets.Values.Update request = service.spreadsheets().values().update
                    (SPREAD_SHEET_ID, "D" + (i + 2), new ValueRange().setRange("D" + (i + 2))
                            .setValues(values));
            request.setValueInputOption("RAW");

            UpdateValuesResponse response = request.execute();
            System.out.println("Updated. Response: " + response);
        }
    }

    private static void createFolder(List<ImageMetadata> extracted) throws Exception {
        String parentFolder = "1F8DIkFgVe7lH_vQz_nsVWhp2_IYqbQe8";
        // Team Drive's parent
        //        String parentFolder = "1XuFvV7bMpVTqGQrn6Vl1z-ejt1POugh_";

        Drive driveService = createDriveService();
        for (ImageMetadata imageMetadata : extracted) {
            createFolderForParent(driveService, imageMetadata, parentFolder);
        }
    }

    private static String createFolderForParent(Drive service, ImageMetadata imageMetadata,
                                                String parentId) throws Exception {
        String month = getNormalizedMonth(imageMetadata.date);
        String year = getNormalizedYear(imageMetadata.date);

        String yearFolderId = getExistsFolder(service, year, parentId);
        if (null == yearFolderId) {
            yearFolderId = createFolderWithoutCheck(service, year, parentId);
        }
        System.out.println("### year folder id: " + yearFolderId + " for " + "date: " +
                imageMetadata.date);

        String monthFolderId = getExistsFolder(service, month, yearFolderId);
        if (null != monthFolderId) {
            return monthFolderId;
        }
        System.out.println("### Creating folder for month: Date: " + imageMetadata.date);
        return createFolderWithoutCheck(service, month, yearFolderId);
    }

    private static String getNormalizedMonth(String date) {
        int month = Integer.parseInt(date.split("/")[0]);
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";

            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";

            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";

            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";

            default:
                return "UNKNOWN";
        }
    }

    private static String getNormalizedYear(String date) {
        int year = Integer.parseInt(date.split("/")[2]);
        if (year >= 2000)
            return String.valueOf(year);

        if (year >= 0 && year <= 30) {
            return String.valueOf(year + 2000);
        }

        if (year > 30) {
            return String.valueOf(1900 + year);
        }
        return String.valueOf(year);
    }

    private static String createFolderWithoutCheck(Drive service, String name, String parentId)
            throws Exception {
        com.google.api.services.drive.model.File file = new com.google.api.services.drive.model
                .File();
        file.setName(name);
        file.setMimeType("application/vnd.google-apps.folder");
        file.setParents(ImmutableList.of(parentId));

        com.google.api.services.drive.model.File file1 = service.files().create(file).setFields
                ("id").execute();
        return file1.getId();
    }

    private static String getExistsFolder(Drive service, String title, String parentId) throws
            IOException {
        Drive.Files.List request;
        request = service.files().list();
        String query = "mimeType='application/vnd.google-apps.folder' AND " + "name='" + title +
                "' AND '" + parentId + "' in parents";
        request = request.setQ(query);
        FileList files = request.execute();
        //        System.out.println("### folder size: " + files.getFiles()
        // .size());
        if (files.getFiles().size() == 0) {
            return null;
        }
        // TODO
        return files.getFiles().get(0).getId();
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

    private static List<ImageMetadata> extractExpenses(Sheets sheetsService) throws Exception {
        ValueRange response = sheetsService.spreadsheets().values().get(SPREAD_SHEET_ID,
                "Transactions!A2:F").execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            ImmutableList.of();
        }
        List<ImageMetadata> imageMetadata = new ArrayList<ImageMetadata>(values.size());
        for (List row : values) {
            //            for (int i = 0; i < row.size(); i++) {
            //                System.out.printf("%s ", row.get(i));
            //            }
            imageMetadata.add(new ImageMetadata(row.get(0).toString(), row.get(2).toString(), ""));
        }
        return imageMetadata;
    }

    private static void printData(Sheets sheetsService) throws Exception {
        ValueRange response = sheetsService.spreadsheets().values().get(SPREAD_SHEET_ID,
                "Transactions!A1:F").execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return;
        }
        for (List row : values) {
            for (int i = 0; i < row.size(); i++) {
                System.out.printf("%s ", row.get(i));
            }
            System.out.println();
        }
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


    private static String fileToString(String fileName) throws Exception {
        InputStream is = new FileInputStream(fileName);
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }

        return sb.toString();
    }
}