#include <fcntl.h>
#include <netdb.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#define DIM_BUFF 256
#define LINE_LENGTH 128

int main(int argc, char *argv[]) {
	int sd, fd_sorg, fd_dest, nread, port, line;
	char buff[DIM_BUFF], car, c;
	char nome_sorg[LINE_LENGTH], nome_dest[LINE_LENGTH], okstr[LINE_LENGTH];
	char terminator = '\0';
	struct hostent *host;
	struct sockaddr_in servaddr;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if (argc != 3)
	{
		printf("error :%s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER -------------------------- */
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	/*
	 * NOTA: gethostbyname restituisce gli indirizzi gia' in formato di rete
	 */
	host = gethostbyname(argv[1]);
	if (host == NULL) {
		printf("%s not found in /etc/hosts\n", argv[1]);
		exit(1);
	}

	nread = 0;
	while (argv[2][nread] != '\0') {
		if ((argv[2][nread] < '0') || (argv[2][nread] > '9')) {
			printf("error: port must be an integer\n");
			exit(2);
		}
		nread++;
	}

	port = atoi(argv[2]);
	if (port < 1024 || port > 65535) {
		printf("error: incorrect port");
		exit(2);
	}

	servaddr.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
	servaddr.sin_port = htons(port);

	/* CREAZIONE SOCKET ------------------------------------
	 * Creo un'unica connessione per l'interazione tra questo client
	 * e il server. Questo consente di essere molto più efficienti,
	 * perché aprire e chiudere una connessione TCP è piuttosto oneroso.
	 */
	sd = socket(AF_INET, SOCK_STREAM, 0);
	if (sd < 0) {
		perror("socket creation");
		exit(1);
	}
	printf("Client: socket creation sd=%d\n", sd);

	/* Operazione di BIND implicita nella connect */
	if (connect(sd, (struct sockaddr_in *)&servaddr, sizeof(struct sockaddr_in)) < 0) {
		perror("connect");
		exit(1);
	}
	printf("Client: connect OK\n");

	/* CORPO DEL CLIENT:
	 ciclo di accettazione di richieste da utente ------- */
	printf("filename or EOF to quit: ");

	while (gets(nome_sorg)) {
		printf("file to open: %s\n", nome_sorg);
		if ((fd_sorg = open(nome_sorg, O_RDONLY)) < 0) {
			perror("open source file");
			printf("filename or EOF to quit: ");
			continue;
		}

		// richiesta e verifica file dest
		printf("dest filename: ");
		if (gets(nome_dest) == 0) {
			// EOF has been reached
			break;
		}
		else {
			if ((fd_dest = open(nome_dest, O_WRONLY | O_CREAT, 0644)) < 0) {
				perror("open file dest");
				printf("filename or EOF to quit: ");
				continue;
			}
			else {
				printf("dest file: %s\n", nome_dest);
			}
		}

		printf("line to delete: ");
		while (scanf("%d", &line) != 1) {
			do {
				c = getchar();
				printf("%c ", c);
			} while (c != '\n');
			printf("insert an int");
			continue;
		}
		gets(okstr);
		printf("line number %d \n", line);

		/* Invio dati al Server */
		printf("sending to server: %d\n", line);
		write(sd, &line, sizeof(int));

		printf("Client: sending file\n");
		while ((nread = read(fd_sorg, buff, DIM_BUFF)) > 0) {
			write(sd, buff, nread);
		}
		// Per segnalare la fine del file non posso usare EOF (chiuderei la conessione)
		// quindi invio uno zero binario. Questo è lecito perché assumiamo file di testo.
		write(sd, &terminator, 1);
		printf("Client: file sent\n");

		printf("Client: receive and print content\n");
		if ((nread = read(sd, &car, sizeof(char))) < 0) {
			perror("error: reading content from server");
			continue;
		}
		while (car != terminator) {
			write(fd_dest, &car, 1);
			write(1, &car, 1);
			if ((nread = read(sd, &car, sizeof(char))) < 0) {
				perror("error: reading content from server");
				continue;
			}
		}
		printf("\nDone!\n");

		/* Chiusura file */
		close(fd_sorg);
		close(fd_dest);

		printf("filename or EOF to quit: ");
	} // while

	close(sd);
	printf("\nClient: termino...\n");
	shutdown(sd, SHUT_RDWR);
	exit(0);
}
