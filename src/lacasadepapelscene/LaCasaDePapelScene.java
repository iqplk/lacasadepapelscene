package lacasadepapelscene;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.scene.AmbientLight;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LaCasaDePapelScene extends Application {

    private final PerspectiveCamera camera = new PerspectiveCamera(true); //انشاء كاميرا ثلاثيه الابعاد

    double gravity = 0; // تتحكم في سرعة سقوط اللاعب عند القفز اذا سالب فوق
    boolean jumping = false; // يمنع اللاعب من القفز 
    double groundY = 8.50; // مستوى الأرض الذي يقف عليه اللاعب

    boolean gameOver = false; // يحدد هل اللعبة مستمرة أم انتهت.

    double laserX = 130;//الليزر يتحرك من اليمين → اليسار
    double laserSpeedX = 1.5;//سرعة تحرك الليزر.

    double laserZ = 70;// يحدد الليزر امام وخلف
    double laserY = 43;//ارتفاع الليزر عن الأرض

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, true); // عرض * ارتفاع

// ---------------- TEXT ----------------
        Text gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font("Arial", 25));
        gameOverText.setFill(Color.BLACK);
        gameOverText.setTranslateX(-75);
        gameOverText.setTranslateY(-50);
        gameOverText.setTranslateZ(70);
        gameOverText.setVisible(false);// غير مرئي بالبدلية

        Text winText = new Text("WIN");
        winText.setFont(Font.font("Arial", 25));
        winText.setFill(Color.BLACK);
        winText.setTranslateX(-75);
        winText.setTranslateY(-50);
        winText.setTranslateZ(70);
        winText.setVisible(false);

// ---------------- ROOM ----------------
        Box floor = new Box(300, 1, 300);// عرض* ارتفاع* عمق
        PhongMaterial floorMaterial = new PhongMaterial(); // نوع من الخام زي مناكير شفاف
        floorMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("/img/floor.jpg")));// ورق الجدران
        floor.setMaterial(floorMaterial);
        floor.setTranslateY(50);

        PhongMaterial red = new PhongMaterial(Color.RED);
        red.setDiffuseColor(Color.web("#C00000"));

        Box backWall = new Box(300, 200, 5);
        backWall.setMaterial(red);
        backWall.setTranslateY(-50);
        backWall.setTranslateZ(150);

        Box leftWall = new Box(5, 200, 300);
        leftWall.setMaterial(red);
        leftWall.setTranslateX(-150);
        leftWall.setTranslateY(-50);

        Box rightWall = new Box(5, 200, 300);
        rightWall.setMaterial(red);
        rightWall.setTranslateX(150);
        rightWall.setTranslateY(-50);
        rightWall.setTranslateZ(10);

        Box ceiling = new Box(300, 5, 300); // السقف
        ceiling.setMaterial(red);
        ceiling.setTranslateY(-150);
// ---------------- LAMP ----------------
        PhongMaterial lampMaterial = new PhongMaterial(Color.WHITE);
        Box hangingWire = new Box(2, 25, 2);
        hangingWire.setMaterial(lampMaterial);
        hangingWire.setTranslateY(-137.5);
        hangingWire.setTranslateZ(80);  // نحيف

        Box lampBox = new Box(50, 5, 50);
        lampBox.setMaterial(lampMaterial);
        lampBox.setTranslateY(-125);
        lampBox.setTranslateZ(80);

        Box lightSurface = new Box(50, 1, 50);// ماله داعي
        PhongMaterial lightSurfaceMaterial = new PhongMaterial(Color.WHITE);
        lightSurface.setMaterial(lightSurfaceMaterial);
        lightSurface.setTranslateY(-122.5);
        lightSurface.setTranslateZ(80);

        PointLight lampLight = new PointLight(Color.web("#CCCCCC")); // نقطه بالثريه
        lampLight.setTranslateY(-120);
        lampLight.setTranslateX(0);
        lampLight.setTranslateZ(80);

// ---------------- STAND + PLATE ----------------
        PhongMaterial plateMaterial = new PhongMaterial();
        plateMaterial.setDiffuseColor(Color.web("#D0D0D0"));

        Box plate = new Box(40, 2, 40);
        plate.setMaterial(plateMaterial);
        plate.setTranslateX(106);
        plate.setTranslateY(-51);
        plate.setTranslateZ(60);

        PhongMaterial standMaterial = new PhongMaterial(Color.BLACK);
        Box stand = new Box(8, 120, 8);
        stand.setMaterial(standMaterial);
        stand.setTranslateX(106);
        stand.setTranslateY(10);
        stand.setTranslateZ(60);

// ---------------- LIGHTING ----------------
        AmbientLight ambientLight = new AmbientLight(Color.web("#404040"));
        ambientLight.setColor(Color.rgb(80, 80, 80)); // 20 كل  م قل كان شادو اغمق

        PointLight spotLight = new PointLight(Color.web("#CCCCCC"));
        spotLight.setColor(Color.rgb(180, 180, 180)); // كل م كان اقل كان ضوء اقل

        spotLight.setTranslateX(120);
        spotLight.setTranslateZ(-120);
        spotLight.setTranslateY(-100);

// ---------------- CAMERA ----------------
        camera.setNearClip(1); //يحدد أقرب مسافة يمكن للكاميرا أن تراها.
        camera.setFarClip(1000);//يحدد أبعد مسافة يمكن للكاميرا رؤيتها.
        camera.setTranslateZ(-350);//يحرك الكاميرا للخلف في الاتجاه Z.
        camera.setTranslateY(-50);//رفع الكاميرا للأعلى
        scene.setCamera(camera);

// ---------------- PLAYER ----------------
        PhongMaterial thiefMaterial = new PhongMaterial();
        thiefMaterial.setDiffuseMap(new Image(getClass().getResourceAsStream("/img/thief.png")));

        Box thief = new Box(60, 80, 1);
        thief.setMaterial(thiefMaterial);
        thief.setTranslateX(-109);
        thief.setTranslateZ(60);

// ---------------- LASER ----------------
        PhongMaterial laserMaterial = new PhongMaterial();
        laserMaterial.setDiffuseColor(Color.RED);
        laserMaterial.setSpecularColor(Color.RED);
        laserMaterial.setSpecularPower(1000);

        Box laser = new Box(1, 1, 200);
        laser.setMaterial(laserMaterial);
        laser.setTranslateY(43);
        laser.setTranslateX(130);
        laser.setTranslateZ(70);

// ---------------- SIREN ----------------
        Image sirenImage = new Image(getClass().getResourceAsStream("/img/siren.png"));
        javafx.scene.image.ImageView sirenView = new javafx.scene.image.ImageView(sirenImage);
        sirenView.setFitWidth(22);
        sirenView.setFitHeight(22);
        sirenView.setTranslateX(106);
        sirenView.setTranslateY(-150);
        sirenView.setTranslateZ(60);

// ---------------- DIAMOND ----------------------
        MeshView diamond = loadOBJAsMesh("/img/diamond.obj");

        PhongMaterial diamondMaterial = new PhongMaterial();
        diamondMaterial.setDiffuseColor(Color.rgb(200, 255, 255, 0.5));
        diamondMaterial.setSpecularColor(Color.WHITE);
        diamondMaterial.setSpecularPower(100);
        diamond.setMaterial(diamondMaterial);

        diamond.setScaleX(40);
        diamond.setScaleY(40);
        diamond.setScaleZ(40);

        diamond.setTranslateX(106);
        diamond.setTranslateY(-70);
        diamond.setTranslateZ(60);

        RotateTransition rotate = new RotateTransition(Duration.seconds(10), diamond);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setByAngle(360);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.play();

        PointLight diamondLight = new PointLight(Color.WHITE);
        diamondLight.setTranslateX(106);
        diamondLight.setTranslateY(-120);
        diamondLight.setTranslateZ(60);

// ---------------- ADD EVERYTHING ----------------
        root.getChildren().addAll(
                floor, backWall, leftWall, rightWall, ceiling,
                hangingWire, lampBox,
                lampLight, ambientLight,
                plate, stand, spotLight,
                gameOverText, thief, laser,
                sirenView, winText,
                diamond, diamondLight, lightSurface
        );

// ---------------- CONTROLS ----------------
        scene.setOnKeyPressed(event -> {

            if (gameOver) {
                return;
            }

            if (event.getCode() == KeyCode.RIGHT) {
                thief.setTranslateX(thief.getTranslateX() + 8);
            } else if (event.getCode() == KeyCode.LEFT) {
                thief.setTranslateX(thief.getTranslateX() - 8);
            } else if (event.getCode() == KeyCode.SPACE && !jumping) {
                gravity = -2.0;
                jumping = true;
            }
        });

// ---------------- GAME -----------------
        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                // لو اللعبة منتهية لا تحدث أي شيء
                if (gameOver) {
                    return;
                }

                // تطبيق الجاذبية على الشخصية
                gravity += 0.06;
                thief.setTranslateY(thief.getTranslateY() + gravity);

                // منع الشخصية من النزول تحت الأرض
                if (thief.getTranslateY() >= groundY) {
                    thief.setTranslateY(groundY);
                    gravity = 0;
                    jumping = false;
                }

                // حركة الليزر من اليمين لليسار
                laserX -= laserSpeedX;

                // عندما يخرج الليزر من الشاشة، يرجع من اليمين
                if (laserX < -140) {
                    laserX = 130;
                }
                laser.setTranslateX(laserX);

                // شرط الاصطدام بين اللص والليزر
                if (Math.abs(thief.getTranslateX() - laserX) < 30
                        && Math.abs(thief.getTranslateY() - laserY) < 35) {

                    // خسارة
                    gameOver = true;
                    gameOverText.setVisible(true);
                    laserSpeedX = 0;
                    gravity = 0;
                }

                // شرط الفوز عندما يصل لمكان محدد
                if (Math.abs(thief.getTranslateX() - 106) < 25
                        && Math.abs(thief.getTranslateZ() - 60) < 25) {

                    // فوز
                    gameOver = true;
                    winText.setVisible(true);
                    laserSpeedX = 0;
                    gravity = 0;
                }
            }
        };

        timer.start();

        primaryStage.setTitle("LaCasaDePapel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

// ---------------- OBJ LOADER ----------------
   public MeshView loadOBJAsMesh(String path) {

    // طباعة المسار للمساعدة في تتبع المشاكل
    System.out.println("Attempting to load resource with path: " + path);

    // محاولة تحميل ملف OBJ من الموارد (resources)
    InputStream is = getClass().getResourceAsStream(path);

    // إذا لم يتم العثور على الملف يرجع MeshView فارغ
    if (is == null) {
        System.out.println("OBJ NOT FOUND! Path: " + path);
        return new MeshView();
    }

    // قوائم لتجميع مكوّنات النموذج
    List<Float> vertices = new ArrayList<>(); // نقاط النموذج (x, y, z)
    List<Float> tex = new ArrayList<>();      // إحداثيات الـ UV texture
    List<Integer> faces = new ArrayList<>();  // وجوه/مثلثات النموذج

    try {
        // قراءة الملف كسطر/سطر
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        while ((line = br.readLine()) != null) {

            // ---------------- نقاط الشكل 3D ----------------
            if (line.startsWith("v ")) {
                // مثال:  v 1.2 3.4 5.6
                String[] p = line.split("\\s+");

                // إضافة كل قيمة على حدة
                vertices.add(Float.parseFloat(p[1]));
                vertices.add(Float.parseFloat(p[2]));
                vertices.add(Float.parseFloat(p[3]));

            // ---------------- إحداثيات Texture ----------------
            } else if (line.startsWith("vt ")) {
                // مثال: vt 0.5 0.2
                String[] p = line.split("\\s+");

                tex.add(Float.parseFloat(p[1]));         // U
                tex.add(1 - Float.parseFloat(p[2]));     // V (مع قلب المحور)

            // ---------------- المثلثات Faces ----------------
            } else if (line.startsWith("f ")) {
                // مثال: f 1/1 2/2 3/3
                String[] p = line.split("\\s+");

                // OBJ faces دائماً ثلاثية
                for (int i = 1; i <= 3; i++) {
                    String[] idx = p[i].split("/");

                    // index النقطة
                    faces.add(Integer.parseInt(idx[0]) - 1);

                    // index ال texture إذا موجود، وإلا 0
                    faces.add(idx.length > 1 && !idx[1].isEmpty()
                              ? Integer.parseInt(idx[1]) - 1
                              : 0);
                }
            }
        }

        // ================= إنشاء الـ mesh =================
        TriangleMesh mesh = new TriangleMesh();

        // تحويل النقاط إلى مصفوفة
        float[] vArr = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            vArr[i] = vertices.get(i);
        }

        // تحويل إحداثيات texture إلى مصفوفة
        float[] tArr;
        if (tex.isEmpty()) {
            // إذا لا يوجد texture أضف قيمة افتراضية
            tArr = new float[]{0, 0};
            tex.add(0f);
            tex.add(0f);
        } else {
            tArr = new float[tex.size()];
            for (int i = 0; i < tex.size(); i++) {
                tArr[i] = tex.get(i);
            }
        }

        // تحويل faces إلى مصفوفة
        int[] fArr = new int[faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            fArr[i] = faces.get(i);
        }

        // تعبئة بيانات الـ mesh
        mesh.getPoints().setAll(vArr);
        mesh.getTexCoords().setAll(tArr);
        mesh.getFaces().setAll(fArr);

        // إرجاع MeshView جاهز للعرض
        return new MeshView(mesh);

    } catch (IOException e) {
        e.printStackTrace();
    }

    // في حالة وجود خطأ يرجع MeshView فارغ
    return new MeshView();
}


    public static void main(String[] args) {
        launch(args);
    }
}
