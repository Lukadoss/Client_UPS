//
// Created by Lukado on 20. 10. 2016.
//

#include "server.h"

void writeHelp();

int main(int argc, char* argv[]) {
    if (argc==2){
        if (strcmp(argv[1], "-default") == 0){
            server *newServer = new server();
            newServer->start();
        }else writeHelp();
    }else if(argc==5){
        if (atoi(argv[2]) > 65535 || atoi(argv[2]) < 1){
            std::cout<<"Port musí být v rozsahu 1-65535"<<std::endl;
            exit(1);
        }
        if (atoi(argv[3]) < 1 || atoi(argv[3]) > 20){
            std::cout<<"Maximální počet místností musí být v rozsahu 1-20"<<std::endl;
            exit(1);
        }
        if (atoi(argv[4]) < 2 || atoi(argv[4]) > 7){
            std::cout<<"Maximální počet hráčů musí být v rozsahu 2-7"<<std::endl;
            exit(1);
        }
        server *newServer = new server(argv[1], atoi(argv[2]), atoi(argv[3]), atoi(argv[4]));
        newServer->start();
    }
    else writeHelp();
}

void writeHelp(){
    std::cout<<"------   Spusťte server s parametry   ------"<<std::endl;
    std::cout<<"./Server -default"<<std::endl;
    std::cout<<"\tUvedení serveru do předdefinovaného nastavení"<<std::endl;
    std::cout<<"./Server <IP> <PORT> <MAX_ROOMS> <MAX_PLAYERS> (./Server 127.0.0.1 2222 2 4)"<<std::endl;
    std::cout<<"\t<IP> - IP adresa server"<<std::endl;
    std::cout<<"\t<PORT> - Port serveru. Musí být v rozsahu 1-65535. Pro porty z rozsahu 1-1023 je nutné zapnout server jako root"<<std::endl;
    std::cout<<"\t<MAX_ROOMS> - Maximální počet vytvořených herních místností. Musí být v rozsahu 1-20"<<std::endl;
    std::cout<<"\t<MAX_PLAYERS> - Maximální počet hráčů v jedné místnosti. Musí být v rozsahu 2-7"<<std::endl;
}