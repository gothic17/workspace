/**
 * na start ka�dej rundy ustawiamy zmienn� gotowo�ci do licytacji i boolean inGame dla ka�dego gracza na true
switch dostaje string postaci Auction-PersonalNumber-Action-X-0-0
robi split po "-" do tablicy
wywo�uje odpowiednie metody ze wzgl�du na 2 element tablicy
3 element tablicy to kwota, musi wyst�pi� sprawdzenie czy gracz posiada tak� kwot�
check - tylko je�li �aden poprzedni gracz nie podbi�, post�j bez utraty prawa do licytacji
bet - postawienie pierwszej stawki po big blind
raise - przebicie najwy�szego zak�adu innego gracza, ten zak�ad staje si� maksymalnym
call - wyr�wnanie do maksymalnego zak�adu (sprawdzi� czy ma tyle kasy, inaczej ten call b�dzie jako all-in)
fold - rezygnacja, zmiana zmiennej gotowo�ci do licytacji
all-in - wej�cie wszystkim co si� ma, gotowo�� do licytacji false, inGame true, przy zwyci�stwie dostanie od ka�dego gracza tyle ile sam postawi�, chyba �e postawili mniej 
 
boolean raised - przy jakim� podbiciu stawki zmiana na true
 * @author Maciej Ubas
 *
 */
public class Auction {
     
    int[] playersMoney; //ammount of money each player has
    private int[] moneyInGame; //amount of money each player put in game
    public int someoneRaised = 0;
    public int someoneBet = 0;
    //int totalMoneyInGame = 0;
    int highestBid = 0;
    private SocketServer server;
    public Table table;
    public int bufferedMoney=0;
 
    Auction(SocketServer serv, Table t) {
        this.server = serv;
        this.table = t;
         
        playersMoney = new int[SocketServer.getLimitOfPlayers()];
        moneyInGame = new int[SocketServer.getLimitOfPlayers()];
        for (int s = 0; s < playersMoney.length; s++) {
            playersMoney[s]=SocketServer.tokens;
            moneyInGame[s] = 0;
        }
        int pNumber=0;
        for(int i=0; i<playersMoney.length; i++){
            pNumber=i+1;
            server.broadcast("Tokens-"+pNumber+"-"+SocketServer.tokens+"-0-0-0");
        }
        if (SocketServer.getLimitOfPlayers() > 2 ) {
            sb(Table.smallBlind, SocketServer.smallBlindBet);
            bb(Table.bigBlind, SocketServer.bigBlindBet);
        }
        else {
            sb(Table.smallBlind, SocketServer.smallBlindBet);
        }
    }
 
 
    public void basicAuction(String str) {
         
        String[] informations = str.split("-");
        int playerNumber = Integer.parseInt(informations[1]);
        int amount = Integer.parseInt(informations[3]);
 
        switch (informations[2]) {
        case ("sb"):
            sb(playerNumber, amount);
            break;
 
        case ("bb"):
            bb(playerNumber, amount);
            break;
 
        case ("check"):
            check(playerNumber);
            break;
 
        case ("bet"):
            bet(playerNumber, amount);
            break;
 
        case ("raise"):
            raise(playerNumber, amount);
            break;
 
        case ("call"):
            call(playerNumber);
            break;
 
        case ("fold"):
            fold(playerNumber);
            break;
 
        case ("allin"):
            allin(playerNumber);
            break;
 
        default:
             
        }
    }
 
    public void fold(int playerNumber){}
     
    public void sb(int playerNumber, int amount) {
        if (playersMoney[playerNumber - 1] > amount) {
            //playersMoney[playerNumber - 1] = playersMoney[playerNumber - 1] + moneyInGame[playerNumber - 1] - amount;
            moneyInGame[playerNumber - 1] = amount;
            
            highestBid = amount;
            someoneBet=1;
            System.out.println("Auction: SMALLBLIND: " + playerNumber + "-" + someoneBet + "-" + someoneRaised + 
                    "-" + highestBid + "-0");
            server.broadcast("SetLastOffer-" + playerNumber + "-" + highestBid + "-0-0-0");
            server.broadcast("LastOffer-" + playerNumber + "-" + highestBid + "-0-0-0");
            server.broadcast("HighestBid-" + playerNumber + "-" + highestBid + "-0-0-0");
            server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
            //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0");
        } 
        else {
            allin(playerNumber);
        }
    }
 
    public void bb(int playerNumber, int amount) {
        if (playersMoney[playerNumber - 1] > amount) {
            //playersMoney[playerNumber - 1] = playersMoney[playerNumber - 1] + moneyInGame[playerNumber - 1] - amount;
            moneyInGame[playerNumber - 1] = amount;
            
            highestBid = amount;
            someoneBet=1;
            System.out.println("Auction: BIGBLIND: " + playerNumber + "-" + someoneBet + "-" + someoneRaised + 
                    "-" + highestBid + "-0");
            server.broadcast("SetLastOffer-" + playerNumber + "-" + highestBid + "-0-0-0");
            server.broadcast("LastOffer-" + playerNumber + "-" + highestBid + "-0-0-0");
            server.broadcast("HighestBid-" + playerNumber + "-" + highestBid + "-0-0-0");
            server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
            //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0");
        } 
        else {
            allin(playerNumber);
        }
    }
 
    public void check(int playerNumber) { // someone checked, nothing happened
        System.out.println("Auction: CHECK: " + playerNumber + "-" + someoneBet + "-" + someoneRaised + 
                "-" + highestBid + "-0");
        server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
        //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0");
    }
 
    public void allin(int playerNumber) {
         
            table.notifiedAuctionPlayers = 1;
            moneyInGame[playerNumber - 1] = playersMoney[playerNumber - 1];
            highestBid=playersMoney[playerNumber - 1];          
            server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
            //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0");
 
    }
    // !!! Possible, that must be added someoneraise == false
    public void bet(int playerNumber, int amount) { // bet available only
                                                        // after big blind, may
                                                        // be the same as big
                                                        // blind or higher, but
                                                        // still counts as bet
            table.notifiedAuctionPlayers = 1;
            //playersMoney[playerNumber - 1] = playersMoney[playerNumber - 1]
                //  + moneyInGame[playerNumber - 1] - amount;
            moneyInGame[playerNumber - 1] = amount;
            
            highestBid = amount;
            //someoneRaised = true;
            someoneBet=1;
            System.out.println("Auction: BET: " + playerNumber + "-" + someoneBet + "-" + someoneRaised + 
                    "-" + highestBid + "-0");
            server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
            //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0");
    }
 
    public void raise(int playerNumber, int amount) {
            table.notifiedAuctionPlayers = 1;
            //playersMoney[playerNumber - 1] = playersMoney[playerNumber - 1]
            //      + moneyInGame[playerNumber - 1] - amount;
            moneyInGame[playerNumber - 1] = amount;
            
            highestBid = amount;
            someoneRaised = 1;
            System.out.println("Auction: RAISE: " + playerNumber + "-" + someoneBet + "-" + someoneRaised + 
                    "-" + highestBid + "-0");
            server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
            //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0");
             
    }
 
    public void call(int playerNumber) {
         
            //playersMoney[playerNumber - 1] = playersMoney[playerNumber - 1]
                //  + moneyInGame[playerNumber - 1] - highestBid;
            moneyInGame[playerNumber - 1] = highestBid;
            System.out.println("Auction: CALL: " + playerNumber + "-" + someoneBet + "-" + someoneRaised + 
                    "-" + highestBid + "-0");
            server.broadcast("Tokens-"+playerNumber+"-"+playersMoney[playerNumber-1]+"-0-0-0");
            //server.broadcast("Auction-Highest-"+highestBid+"-0-0-0"); 
         
    }
 
    // dzia�a na tablicy zwracanej przez wy�onienie zwyci�zcy z GameLogic
    public int[] whoWon(int[] winners) {
    	System.out.println("AUCTION: winners= "  + winners[0] + "-" + winners[1] + "-" + winners[2]);
         
        int totalMoney = 0; // total ammount of money winners put in game
 
        for (int s = 0; s < moneyInGame.length; s++) {
            totalMoney += moneyInGame[s];
        }
        System.out.println("TOTAL MONEY: " + totalMoney);
        for (int i = 0; i <moneyInGame.length; i++) {
        	playersMoney[i] = playersMoney[i] - moneyInGame[i];
        	System.out.println("PLAYERS MONEY: " + playersMoney[i]);
        	}
 
       if(winners.length==1){
    	   playersMoney[winners[0]]+=totalMoney + bufferedMoney;
    	   bufferedMoney=0;
    	   for(int i=1; i<moneyInGame.length; i++){
    		   server.broadcast("Tokens-"+i+"-"+playersMoney[i-1]+"-0-0-0");
    		   System.out.println("PLAYERS MONEY2: " + playersMoney[i]);
    	   }
    	   
       }
       else{
    	   bufferedMoney+=totalMoney;
    	   for(int i=1; i<=moneyInGame.length; i++){
    		   server.broadcast("Tokens-"+i+"-"+playersMoney[i-1]+"-0-0-0");
    		   System.out.println("PLAYERS MONEY3: " + playersMoney[i]);
    	   }
       }
        return winners;
    }
}