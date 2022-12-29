#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STRING_LENGTH 256

int main(int argc, char *argv[]) {
	char *file_in, *prefix;
	char err[MAX_STRING_LENGTH], buffer[MAX_STRING_LENGTH];

	int nread, i, fd;
	char read_char;

	// CHECK ARGS
	if (argc < 2 || argc > 3) {
		printf("error: %s prefix < filename \n OR \n %s prefix filename\n", argv[0], argv[0]);
		return EXIT_FAILURE;
	}

	else if (argc == 2) {
		printf("invocation without filename passed...\n");
		prefix = argv[1];

		// 0 Standard input (stdin)
		fd = 0;
	}
	else if (argc == 3) {
		printf("invocation with filename passed...\n");
		prefix = argv[1];
		file_in = argv[2];

		fd = open(file_in, O_RDONLY);
		if (fd < 0) {
			perror("p0: cannot open selected file");
			return EXIT_FAILURE;
		}
	}

	while ((nread = read(fd, &read_char, sizeof(char)))) { // till EOF
		if (nread < 0) {
			perror("error: somthing bad happened while reading file");
			return EXIT_FAILURE;
		}

		// write here your logic

		// end logic

		printf("%c", read_char);

	}

	close(fd);
	return EXIT_SUCCESS;
}
