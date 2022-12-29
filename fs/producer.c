#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STRING_LENGTH 256

int main(int argc, char *argv[]) {
	int   fd, written;
	char *file_out;
	char  riga[MAX_STRING_LENGTH];

	file_out = argv[1];

	fd = open(file_out, O_WRONLY | O_CREAT | O_TRUNC, 00640);
	if (fd < 0) {
		perror("p0: cannot open/create file");
		return EXIT_FAILURE;
	}

	printf("Insert new lines or EOF [CTRL^D] to close the program\n");

	while (gets(riga)) { // Reading line by line from input
		riga[strlen(riga)]     = '\n'; // adding \n at the end of file
		riga[strlen(riga) + 1] = '\0'; // adding \0 to make a correct string

		written = write(fd, riga, strlen(riga));
		if (written < 0) {
			perror("p0: error while writing on file");
			return EXIT_FAILURE;
		}
	}
	close(fd);
	return EXIT_SUCCESS;
}