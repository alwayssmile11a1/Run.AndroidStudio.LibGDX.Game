package noshanabi.game;

/**
 * Created by 2SMILE2 on 22/11/2017.
 */

public final class Resourses {

    //----------WORLD RELATED VARIABLES-------------
    public static int WORLDWIDTH = 800;
    public static int WORLDHEIGHT = 400;
    public static float PPM = 100f;


    //-------------SOUND ------------------------------
    public static String ExplosionSound = "audios/explosion1.wav";
    public static String CheckpointSound = "audios/checkpoint.wav";
    public static String ClickSound = "audios/click.ogg";

    //----------EFFECT----------------------------------
    public static String ExplosionEffect1 = "effects/explosion.p";
    public static String ExplosionEffect2 = "effects/explosion2.p";
    public static String ExplosionEffect3 = "effects/explosion3.p";
    public static String ParticleImageDir = "effects";


    //--------JSON---------------------------------
    public static final String ButtonSkinTextureAtlas = "skins/skinbutton.atlas";
    public static String ButtonSkinJSON = "skins/skinbutton.json";
    public static final String ButtonSkinTextureAtlas2 = "skins/skinbutton2.atlas";
    public static String ButtonSkinJSON2 = "skins/skinbutton2.json";
    public static final String FacebookSkinTextureAtlas = "skins/skinfacebooklogin.atlas";
    public static String FacebookLoginSkinJSON = "skins/skinfacebooklogin.json";
    public static final String GoogleSkinTextureAtlas = "skins/skingooglelogin.atlas";
    public static String GoogleLoginSkinJSON = "skins/skingooglelogin.json";
    public static final String SignoutSkinTextureAtlas = "skins/skinsignoutbutton.atlas";
    public static String SignoutSkinJSON = "skins/skinsignoutbutton.json";


    //---------OTHER------------------------------
    public static final String WhiteRectangle = "images/WhiteRectangle.png";


    //------------------PLAYER -----------------------
    public static String Player1 = "players/alienYellow_square.png";
    public static String PlayerSheet = "players/spritesheet_aliens.png";
    public static String PlayerSheetXML = "players/spritesheet_aliens.xml";



    //-----------------ENEMIES-------------------------
    public static String EnemiesSpriteSheet = "enemies/spritesheet_enemies.png";
    public static String Saw = "enemies/saw_move.png";
    public static String SawHalf1 = "enemies/sawHalf.png";
    public static String SawHalf2 = "enemies/sawHalf_move.png";


    //----------------SERVER ---------------------------
    public static boolean UseLocal = true; //if false, use web server
    public static String WebServerUri = "https://runandroidstudiolibgdx.herokuapp.com";
    public static String LocalServerUri = "http://localhost:5000";


    //---------------BUTTON-------------------------
    public static String ReturnScreenButton = "images/rightarrow.png";
    public static String SignOutButton = "images/signout.png";


    //----------------MENU SCREEN---------------------
    public static String MenuScreenBackground = "images/BlueBackground.png";
    public static String SinglePlayButton = "images/singleplayer.png";
    public static String MultiPlayButton = "images/massiveMultiplayer.png";



    //----------CREATE ROOM SCREEN------------------
    public static String CreateRoomBackground = "images/BlueBackground.png";



    //----------FIND ROOM SCREEN-------------------
    public static String FindRoomBackground = "images/BlueBackground.png";



    //----------LOGIN SCREEN ---------------------
    public static String LoginScreenBackground = "images/BlueBackground.png";
    public static String GoogleLoginButton = "images/googleloginbutton.png";
    public static String FacebookLoginButton = "images/facebookloginbutton.png";


    //----------MAP SELECTION SCREEN --------------
    public static String MapSelectionBackground = "images/BlueBackground.png";
    public static String NextMapButton = "images/nextarrow.png";


    //---------ROOM JOINED SCREEN -------------------
    public static String RoomJoinedBackground = "images/BlueBackground.png";



    //--------GAME FINISHED UI ----------------------
    public static String PauseButton = "images/pausebutton.png";
    public static String GameFinishedBackground = "images/WhiteRectangle.png";
    public static String ReviewButton = "images/youtube.png";
    public static String MenuButton = "images/menubutton.png";
    public static String ReplayButton = "images/replaybutton.png";
    public static String InspectButton = "images/search.png";


    //-----------IN GAME UI -------------------------
    public static String InGameBackground = "images/WhiteRectangle.png";
    public static String ContinueButton = "images/continuebutton.png";

}
