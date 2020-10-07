/*
    AP Assignment 3 (Mafia)
    Name: Dhairya Chaudhary
    Roll No.: 2019035
 */

import java.util.*;
import java.lang.Math;

class ContactList<T> {
    /*
    This class represents the other team members that a player is aware of.
    For example, mafias are aware of mafias, detectives of detectives etc.

    ATTRIBUTES:
        * contacts: ArrayList with all the players of the type

    METHODS:
        * Contains the class constructor
        * add()
        * remove()
        * get()
        * getSize()
     */

    private ArrayList<T> contacts;

    public ContactList() {
        //Class Constructor
        contacts = new ArrayList<T>();
    }

    public void add(T o) {
        //Adds element to list
        contacts.add(o);
    }

    public void remove(T o) {
        //Removes element from list
        contacts.remove(o);
    }

    public T get(int i) {
        //Returns element at given index
        return contacts.get(i);
    }

    public int getSize() {
        //Returns size of the list
        return contacts.size();
    }
}

class newException extends Exception {
    /*
    This class is used to raise new exceptions

    METHODS:
        * Contains a class constructor
     */

    public newException(String message) {
        //class Constructor
        super(message);
    }
}

abstract class Player implements Comparable<Player> {
    /*
    This class represents a player of the the game. This is the parent class of all the 4 player types.

    ATTRIBUTES:
        * HP: Represents the health points of a player [Integer].
        * playerNumber: Unique number associated with the player [Integer].
        * HPLimit: Limit below which HP cannot fall (0) [Integer].

    METHODS:
        * Contains accessors of all attributes and mutators for those that can be modified.
        * Contains a class constructor
        * getHP()
        * getHPLimit()
        * getPlayerNumber()
        * setHP()
        * compareTo()
        * equals()
        * reduceHP()
        * getType()
        * play()
        * simulate()
    */

    private int HP;
    private final int playerNumber;
    private final int HPLimit;

    {
        //Initialization Block
        HPLimit = 0;
    }

    public Player(int number) {
        //Class constructor
        this.playerNumber = number;
    }

    //Accessors

    protected int getHP() {
        return HP;
    }

    protected int getHPLimit() {
        return HPLimit;
    }

    protected int getPlayerNumber() {
        return playerNumber;
    }

    //Mutators

    protected void setHP(int HP) {
        this.HP = HP;
    }

    public int compareTo(Player o) {
        //Object Comparison
        if (HP == o.HP) return 0;
        else if (HP < o.HP) return -1;
        else return 1;
    }

    public boolean equals(Object o1) {
        //Object equality check
        if (o1 != null && getClass() == o1.getClass()) {
            Player o = (Player) o1; //type casting
            return (playerNumber == o.playerNumber);
        } else {
            return false;
        }
    }

    //Abstract methods
    public abstract void reduceHP(int amount);

    public abstract String getType();

    public abstract int play();
}

class Commoner extends Player {
    /*
    This class represents a player of the type commoner. This is a child class of all the Player class.

    METHODS:
        * Contains a class constructor
        * reduceHP()
        * getType()
        * play()
        * simulate()
    */

    public Commoner(int n) {
        //Class constructor
        super(n);
        super.setHP(1000);
    }

    @Override
    public void reduceHP(int amount) {
        this.setHP(this.getHP() - amount);
        if (this.getHP() < 0) {
            this.setHP(0);
        }
    }

    @Override
    public String getType() {
        //Returns type
        return "Commoner";
    }

    @Override
    public int play() {
        return 0;
    }
}

class Mafia extends Player {
    /*
    This class represents a player of the type Mafia. This is a child class of all the Player class.

    ATTRIBUTES:
        * gang: This is the list of all the Mafia gang members [ContactList].

    METHODS:
        * Contains a class constructor
        * reduceHP()
        * getType()
        * play()
        * simulate()
        * mafiaHPDecrement
        * gangSize()
        * removeGangMember()
        * memberAt()
        * mafiaHP()
        * kill()
    */

    private static ContactList<Mafia> gang = new ContactList<Mafia>();

    public Mafia(int n) {
        //Class constructor
        super(n);
        super.setHP(2500);
        gang.add(this);
    }

    @Override
    public void reduceHP(int amount) {
        amount = amount / gangSize();
        this.setHP(this.getHP() - amount);
        if (this.getHP() < 0) {
            this.setHP(0);
        }
    }

    @Override
    public String getType() {
        return "Mafia";
    }

    @Override
    public int play() {
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }

    public static void mafiaHPDecrement(Player target, int amount) {
        //This function handles the damage to the gang after a target is attacked.
        int i = 0;
        while (i != gangSize()) {
            int a = gang.get(i).compareTo(target);
            if (a == -1) {
                amount = amount + ((amount / gangSize()) + gang.get(i).getHP());
                gang.get(i).setHP(0);
            } else {
                gang.get(i).reduceHP(amount);
            }
            i++;
        }
    }

    public static int gangSize() {
        //Returns size of the gang
        return gang.getSize();
    }

    public static void removeGangMember(Mafia member) {
        //Removed a gang members (who has been voted out)
        gang.remove(member);
    }

    public static Mafia memberAt(int position) {
        //Returns member at a position
        return gang.get(position);
    }

    public static int MafiaHP() {
        //Returns total HP of the gang
        int netHP = 0;
        int i = 0;
        while (i != gangSize()) {
            netHP = netHP + gang.get(i).getHP();
            i++;
        }
        return netHP;
    }

    public static int kill(HashMap<Integer, Player> activePlayers, Player user) {
        //Randomly picks a player to kill (not a mafia)

        HashMap<Integer, Integer> temp = new HashMap<>();
        Random randomGenerator = new Random();

        int i = 0;

        //Creating hashmap of all non-mafia players (unique player number:player)
        for (Map.Entry<Integer, Player> entry : activePlayers.entrySet()) {
            if (!(entry.getValue().getType().equals("Mafia"))) {
                temp.put(entry.getKey(), 0);
            }
        }

        //Voting process to select the player to attack
        i = 0;
        while (i != gang.getSize()) {
            if (gang.get(i).equals(user)) {
                System.out.print("Select a person to kill: ");
                boolean flag1 = true;
                while (flag1) {
                    try {
                        int p = user.play();
                        temp.put(p, temp.get(p) + 1);
                        flag1 = false;
                    } catch (Exception e) {
                        System.out.println("Invalid input");
                    }
                }
            } else {
                int p = randomGenerator.nextInt(temp.size()) + 1;
                int lv = 1;
                for (Map.Entry<Integer, Integer> element : temp.entrySet()) {
                    lv++;
                    if (lv == p + 1) {
                        temp.put(element.getKey(), element.getValue() + 1);
                    }
                }

            }
            i++;
        }

        //Finding player with maximum votes and returning their unique number
        int max = 0;
        int maxi = 0;
        for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxi = entry.getKey();
            }
        }
        return maxi;
    }
}

class Healer extends Player {
    /*
    This class represents a player of the type Healer. This is a child class of all the Player class.

    ATTRIBUTES:
        * healers: This is the list of all the active healers [ContactList].

    METHODS:
        * Contains a class constructor
        * reduceHP()
        * getType()
        * play()
        * simulate()
        * numHealers()
        * removeGangMember()
        * healerAt()
        * heal()
    */

    private static ContactList<Healer> healers = new ContactList<Healer>();

    public Healer(int n) {
        //Class constructor
        super(n);
        super.setHP(800);
        healers.add(this);
    }

    @Override
    public void reduceHP(int amount) {
        this.setHP(this.getHP() - amount);
        if (this.getHP() < 0) {
            this.setHP(0);
        }
    }

    @Override
    public String getType() {
        //Returns type of player
        return "Healer";
    }

    @Override
    public int play() {
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }

    public static int numHealers() {
        //Returns number of active healers
        return healers.getSize();
    }

    public static void removeGangMember(Healer member) {
        //Removes a healer (killed/voted out)
        healers.remove(member);
    }

    public static Healer healerAt(int position) {
        //Returns healer at specified position
        return healers.get(position);
    }


    public static int heal(HashMap<Integer, Player> activePlayers, Player user) {
        //Randomly picks a player to heal
        HashMap<Integer, Integer> temp = new HashMap<>();
        Random randomGenerator = new Random();

        //Creating hashmap of all players (unique player number:player)
        for (Map.Entry<Integer, Player> entry : activePlayers.entrySet()) {
            temp.put(entry.getKey(), 0);
        }

        //Voting process
        int i = 0;
        while (i != healers.getSize()) {
            if (healers.get(i).equals(user)) {
                System.out.print("Choose a player to heal: ");
                boolean flag1 = true;
                while (flag1) {
                    try {
                        int p = user.play();
                        temp.put(p, temp.get(p) + 1);
                        flag1 = false;
                    } catch (Exception e) {
                        System.out.println("Invalid input (Only active players who are not detectives can be questioned)");
                    }
                }
            } else {
                int p = randomGenerator.nextInt(temp.size()) + 1;
                int lv = 1;
                for (Map.Entry<Integer, Integer> element : temp.entrySet()) {
                    lv++;
                    if (lv == p + 1) {
                        temp.put(element.getKey(), element.getValue() + 1);
                    }
                }

            }
            i++;
        }

        //Checking to see which player got maximum votes
        int max = 0;
        int maxi = 0;
        for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxi = entry.getKey();
            }
        }
        return maxi;
    }
}

class Detective extends Player {
    /*
    This class represents a player of the type Detective. This is a child class of all the Player class.

    ATTRIBUTES:
        * squad: This is the list of all the active detectives [ContactList].

    METHODS:
        * Contains a class constructor
        * reduceHP()
        * getType()
        * play()
        * simulate()
        * squadSize()
        * removeDetective()
        * detectiveAt()
        * question()
    */


    private static ContactList<Detective> squad = new ContactList<Detective>();

    public Detective(int n) {
        //Class constructor
        super(n);
        super.setHP(800);
        squad.add(this);
    }

    @Override
    public void reduceHP(int amount) {
        this.setHP(this.getHP() - amount);
        if (this.getHP() < 0) {
            this.setHP(0);
        }
    }

    @Override
    public String getType() {
        //Returns player type
        return "Detective";
    }

    @Override
    public int play() {
        Scanner input = new Scanner(System.in);
        return input.nextInt();
    }

    public static int squadSize() {
        //Returns number of active detectives
        return squad.getSize();
    }

    public static void removeDetective(Detective member) {
        //Removes detective from squad (killed or voted out)
        squad.remove(member);
    }

    public static Detective detectiveAt(int position) {
        //Returns detective at given position
        return squad.get(position);
    }

    public static int question(HashMap<Integer, Player> activePlayers, Player user) {
        //Randomly picks a player to test and tests them
        HashMap<Integer, Integer> temp = new HashMap<>();
        Random randomGenerator = new Random();

        //Creating hashmap of all players (unique player number:player)
        int i = 0;
        for (Map.Entry<Integer, Player> entry : activePlayers.entrySet()) {
            if (!(entry.getValue().getType().equals("Detective"))) {
                temp.put(entry.getKey(), 0);
            }
        }

        //Voting process
        i = 0;
        while (i != squad.getSize()) {
            if (squad.get(i).equals(user)) {
                System.out.print("Choose a player to test: ");
                boolean flag1 = true;
                while (flag1) {
                    try {
                        int p = user.play();
                        temp.put(p, temp.get(p) + 1);
                        flag1 = false;
                    } catch (Exception e) {
                        System.out.println("Invalid input (Only active players who are not detectives can be questioned)");
                    }
                }
            } else {
                int p = randomGenerator.nextInt(temp.size()) + 1;
                int lv = 1;
                for (Map.Entry<Integer, Integer> element : temp.entrySet()) {
                    lv++;
                    if (lv == p + 1) {
                        temp.put(element.getKey(), element.getValue() + 1);
                    }
                }

            }
            i++;
        }

        //Finding player with maximum votes
        int max = 0;
        int maxi = 0;
        for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxi = entry.getKey();
            }
        }

        int retVal = 0;
        if (activePlayers.get(maxi).getType().equals("Mafia")) {
            retVal = maxi;
            if (user.getType().equals("Detective")) {
                System.out.println("Player" + maxi + " is a mafia");
            }
        } else {
            if (user.getType().equals("Detective")) {
                System.out.println("Player" + maxi + " is not a mafia");
            }
        }
        return retVal;
    }
}

class Round {
    /*
    This class represents one round of the game.

    ATTRIBUTES:
        * roundNumber: Round number [Integer].
        * activePlayers: Stores each player alongside their unique player number [HashMap<Integer, Player>].
        * user: The player playing the game [Player].

    METHODS:
        * Contains a class constructor
        * mafiasTurn()
        * detectivesTurn()
        * healersTurn()
        * voteKick()
        * startRound()
    */

    private final int roundNumber;
    private HashMap<Integer, Player> activePlayers;
    private Player user;

    Scanner input = new Scanner(System.in);
    Random randomGenerator = new Random();

    public Round(int roundNumber, HashMap<Integer, Player> activePlayers, Player user) {
        //Class constructor
        this.roundNumber = roundNumber;
        this.activePlayers = activePlayers;
        this.user = user;
    }

    public int mafiasTurn() {
        //Mafia's action in the round
        if (Mafia.gangSize() > 0) {
            return Mafia.kill(activePlayers, user);
        }
        return 0;
    }

    public int detectivesTurn() {
        //Detective's action in the round
        if (Detective.squadSize() > 0) {
            int isMafia = Detective.question(activePlayers, user);
            return isMafia;
        } else return 0;
    }

    public int healersTurn() {
        //Healer's action in the round
        if (Healer.numHealers() > 0) {
            return Healer.heal(activePlayers, user);
        }
        return 0;
    }

    public int voteKick() {
        //Thus function implements the voting process in which every player takes part

        HashMap<Integer, Integer> temp = new HashMap<>();

        int i = 0;

        for (Map.Entry<Integer, Player> entry : activePlayers.entrySet()) {
            temp.put(entry.getKey(), 0);
        }

        //Voting process
        for (Map.Entry<Integer, Player> entry : activePlayers.entrySet()) {
            if (entry.getValue().equals(user)) {
                System.out.print("Select a person to vote out: ");
                boolean flag1 = true;
                while (flag1) {
                    try {
                        int p = input.nextInt();
                        temp.put(p, temp.get(p) + 1);
                        flag1 = false;
                    } catch (Exception e) {
                        System.out.println("Invalid input");
                    }
                }
            } else {
                int p = randomGenerator.nextInt(temp.size() + 1);
                int lv = 1;

                for (Map.Entry<Integer, Integer> element : temp.entrySet()) {
                    lv++;
                    if (lv == p + 1) {
                        temp.put(element.getKey(), element.getValue() + 1);
                    }
                }
            }
        }

        //Finds player who received the maximum votes and removes them from active players. This player won't be present in next round
        int max = 0;
        int maxi = 0;
        for (Map.Entry<Integer, Integer> entry : temp.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxi = entry.getKey();
            }
        }

        System.out.println("Player" + maxi + " has been voted out");
        if (activePlayers.get(maxi).getType().equals("Mafia")) {
            Mafia.removeGangMember((Mafia) activePlayers.get(maxi));
        } else if (activePlayers.get(maxi).getType().equals("Detective")) {
            Detective.removeDetective((Detective) activePlayers.get(maxi));
        } else if (activePlayers.get(maxi).getType().equals("Healer")) {
            Healer.removeGangMember((Healer) activePlayers.get(maxi));
        }
        activePlayers.remove(maxi);
        return 0;
    }

    public void startRound() {
        //The actions/gameplay of a round
        System.out.println("Round " + roundNumber + ":");

        System.out.print(activePlayers.size() + " players are remaining: ");
        for (Map.Entry<Integer, Player> entry : activePlayers.entrySet()) {
            System.out.print("player" + entry.getKey() + " ");
        }
        System.out.println("are alive.");

        //Mafia's turn
        if (!user.getType().equals("Mafia")) {
            System.out.println("Mafias have picked a target.");
        }
        int target = mafiasTurn();

        //Detective's turn
        if (!user.getType().equals("Detective")) {
            System.out.println("Detectives have chosen someone to question.");
        }
        int isMafia = detectivesTurn();

        //Healer's turn
        if (!user.getType().equals("Healer")) {
            System.out.println("Healers have chosen someone to heal.");
        }
        int healedPlayer = healersTurn();

        System.out.println("--End of Actions--");

        //Implementation of action of healers and mafia
        if (target != 0) {
            if (activePlayers.get(target).getHP() > Mafia.MafiaHP()) {
                try {
                    activePlayers.get(target).reduceHP(Mafia.MafiaHP());
                    activePlayers.get(healedPlayer).setHP(activePlayers.get(healedPlayer).getHP() + 500);
                } catch (Exception e) {

                } finally {
                    System.out.println("No one died. (Player HP too high)");
                }
            } else if (target == healedPlayer) {
                Mafia.mafiaHPDecrement(activePlayers.get(target), activePlayers.get(target).getHP());
                activePlayers.get(target).setHP(activePlayers.get(target).getHP() + 500);
                System.out.println("No one died. (Target healed)");
            } else if (healedPlayer != 0) {
                Mafia.mafiaHPDecrement(activePlayers.get(target), activePlayers.get(target).getHP());
                System.out.println("Player" + target + " has been killed.");
                if (activePlayers.get(target).getType().equals("Detective")) {
                    Detective.removeDetective((Detective) activePlayers.get(target));
                } else if (activePlayers.get(target).getType().equals("Healer")) {
                    Healer.removeGangMember((Healer) activePlayers.get(target));
                }
                activePlayers.get(healedPlayer).setHP(activePlayers.get(healedPlayer).getHP() + 500);
                activePlayers.remove(target);
            } else {
                Mafia.mafiaHPDecrement(activePlayers.get(target), activePlayers.get(target).getHP());
                System.out.println("Player" + target + " has been killed");
                if (activePlayers.get(target).getType().equals("Detective")) {
                    Detective.removeDetective((Detective) activePlayers.get(target));
                } else if (activePlayers.get(target).getType().equals("Healer")) {
                    Healer.removeGangMember((Healer) activePlayers.get(target));
                }
                activePlayers.remove(target);
            }
        }

        //If detective didn't catch a mafia during questioning, voting occurs
        if (isMafia != 0) {
            System.out.println("Player" + isMafia + " has been voted out.");
            Mafia.removeGangMember((Mafia) activePlayers.get(isMafia));
            activePlayers.remove(isMafia);
        } else {
            voteKick();
        }

        System.out.println("--End of Round " + roundNumber + "--");
        System.out.println();

    }

}

class Game {
    /*
    This is the game class. The game will run till the mafias either win or lose.

    METHODS:
        * Contains a class constructor
        * checkGameCondition()
        * start()
        * setup()
        * voteKick()
        * startRound()
    */

    Scanner input = new Scanner(System.in);
    Random randomGenerator = new Random();

    public boolean checkGameCondition(HashMap<Integer, Player> playersList) {
        if (Mafia.gangSize() == 0) {
            //Game ends if all mafias are voted out
            System.out.println("Game over.");
            System.out.println("The Mafias have lost");
            return false;
        } else if (Mafia.gangSize() >= playersList.size() - Mafia.gangSize()) {
            //Game ends if 1:1 ratio is achieved
            System.out.println("Game Over.");
            System.out.println("The Mafias have won");
            return false;
        }
        //Game continues otherwise
        return true;
    }

    public void start(HashMap<Integer, Player> playersList, Player user) {
        //The actual gameplay begins after successful setup
        int i = 1;
        while (checkGameCondition(playersList)) {
            Round nextRound = new Round(i, playersList, user);
            nextRound.startRound();
            i++;
        }
    }

    public void setup() {
        //Setting up the game
        System.out.println("Welcome to Mafia");
        System.out.print("Enter Number of Players: ");

        int n = 0;
        boolean flag = true;

        //Taking number of players as input (defensively)
        while (flag) {
            try {
                n = input.nextInt();
                if (n < 6) {
                    throw new newException("invalid input");
                }
                flag = false;
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer value");
                input.next();
            } catch (newException e) {
                System.out.println("There must be at least six players");
            }
        }

        HashMap<Integer, Player> playersList = new HashMap<>();
        ArrayList<Integer> temp = new ArrayList<Integer>();

        int i = 1;
        while (i != n + 1) {
            temp.add(i);
            i++;
        }

        int index;

        String commoners = "Commoners:";
        String mafias = "Mafias:";
        String detectives = "Detectives:";
        String healers = "Healers:";

        //Menu to pick player type
        System.out.println("Choose a Character\n" +
                "1) Mafia\n" +
                "2) Detective\n" +
                "3) Healer\n" +
                "4) Commoner\n" +
                "5) Assign Randomly");

        int x = 0;

        flag = true;

        //Taking player type as input (defensively)
        while (flag) {
            try {
                x = input.nextInt();
                if (x < 1 || x > 5) {
                    throw new newException("invalid input");
                }
                flag = false;
            } catch (InputMismatchException e) {
                System.out.println("Please provide valid input");
                input.next();
            } catch (newException e) {
                System.out.println("Please provide valid input");
            }
        }

        //Creating player and assigning them a role
        Player user;
        index = (randomGenerator.nextInt(temp.size()));
        int nm = 0, nd = 0, nh = 0;

        if (x == 1) {
            user = new Mafia(temp.get(index));
            nm = 1;
            mafias = mafias + " player" + temp.get(index);
        } else if (x == 2) {
            user = new Detective(temp.get(index));
            nd = 1;
            detectives = detectives + " player" + temp.get(index);
        } else if (x == 3) {
            user = new Healer(temp.get(index));
            nh = 1;
            healers = healers + " player" + temp.get(index);
        } else if (x == 4) {
            user = new Commoner(temp.get(index));
            commoners = commoners + " player" + temp.get(index);
        } else {
            int type = randomGenerator.nextInt(4);
            if (type == 0) {
                user = new Mafia(temp.get(index));
                nm = 1;
                mafias = mafias + " player" + temp.get(index);
            } else if (type == 1) {
                user = new Detective(temp.get(index));
                nd = 1;
                detectives = detectives + " player" + temp.get(index);
            } else if (type == 2) {
                user = new Healer(temp.get(index));
                nh = 1;
                healers = healers + " player" + temp.get(index);
            } else {
                user = new Commoner(temp.get(index));
                commoners = commoners + " player" + temp.get(index);
            }
        }
        System.out.println("You are player" + temp.get(index));
        System.out.print("You are a " + user.getType());

        if (user.getType().equals("Commoner")) {
            System.out.println();
        }

        playersList.put(temp.get(index), user);
        temp.remove(index);

        //Generating other players
        int sum = 1;
        i = 0;
        while (i < Math.floor(n / 5) - nm) {
            index = (randomGenerator.nextInt(temp.size()));
            playersList.put(temp.get(index), new Mafia(temp.get(index)));
            mafias = mafias + " player" + temp.get(index);
            temp.remove(index);
            i++;
            sum++;
        }

        i = 0;
        while (i < Math.floor(n / 5) - nd) {
            index = (randomGenerator.nextInt(temp.size()));
            playersList.put(temp.get(index), new Detective(temp.get(index)));
            detectives = detectives + " player" + temp.get(index);
            temp.remove(index);
            i++;
            sum++;
        }

        i = 0;
        while (i < Math.max(1, Math.floor(n / 10)) - nh) {
            index = (randomGenerator.nextInt(temp.size()));
            playersList.put(temp.get(index), new Healer(temp.get(index)));
            healers = healers + " player" + temp.get(index);
            temp.remove(index);
            i++;
            sum++;
        }

        i = 0;
        while (i < n - sum) {
            index = (randomGenerator.nextInt(temp.size()));
            playersList.put(temp.get(index), new Commoner(temp.get(index)));
            commoners = commoners + " player" + temp.get(index);
            temp.remove(index);
            i++;
        }

        if (nm == 1) {
            if (Mafia.gangSize() == 1) {
                System.out.print(". You are the only mafia.");
            } else {
                System.out.print(". The other Mafia are ");
                int lv = 0;

                while (lv != Mafia.gangSize()) {
                    if (Mafia.memberAt(lv).equals(user)) {

                    } else {
                        System.out.print("player" + Mafia.memberAt(lv).getPlayerNumber() + " ");
                    }
                    lv++;
                }
            }
            System.out.println();
        } else if (nd == 1) {

            if (Detective.squadSize() == 1) {
                System.out.print(". You are the only detective.");
            } else {
                System.out.print(". The other detectives are ");

                int lv = 0;
                while (lv != Detective.squadSize()) {
                    if (Detective.detectiveAt(lv).equals(user)) {

                    } else {
                        System.out.print("player" + Detective.detectiveAt(lv).getPlayerNumber() + " ");
                    }
                    lv++;
                }
            }
            System.out.println();
        } else if (nh == 1) {
            if (Healer.numHealers() == 1) {
                System.out.print(". You are the only healer.");
            } else {
                System.out.print(". The other healers are ");
                int lv = 0;
                while (lv != Healer.numHealers()) {
                    if (Healer.healerAt(lv).equals(user)) {

                    } else {
                        System.out.print("player" + Healer.healerAt(lv).getPlayerNumber() + " ");
                    }
                    lv++;
                }
            }
            System.out.println("\n");
        }

        //Starts gameplay
        start(playersList, user);

        //Reveals roles after game ends
        System.out.println(mafias);
        System.out.println(detectives);
        System.out.println(healers);
        System.out.println(commoners);
    }

}

public class MafiaGame {
    //This class calls the main method
    public static void main(String[] args) {
        Game newGame = new Game();
        newGame.setup();
    }
}
