import sys, getopt
from os import listdir
from os.path import isfile, join, basename
#used for turning .java and .scala files to javascript string array
#Usage: python fileToArray.py -d PathToDirectory or python fileToArray.py -f PathToFile

outdir = "/tmp/" #output path
def main(argv):
	directory = ''
	filename = ''

	try:
		opts, args = getopt.getopt(argv,"d:f:",["dir=","file="])
	except getopt.GetoptError:
		print 'python fileToJsArray.py -d <directory path>  or -f <file path>'
		sys.exit(2)
	for opt, arg in opts:
		if opt in ("-d", "--dir"):
			directory = arg
		elif opt in ("-f", "--file"):
			filename = arg

	if filename == '' and directory != '':
		for f in listdir(directory):
			if isfile(join(directory, f)):
				readFromFile(join(directory, f))
	elif directory == '' and filename != '':
		readFromFile(filename)
	else:
		print 'python fileToJsArray.py -d <directory path>  or -f <file path>'
		sys.exit(2)

def readFromFile(fr):
	ff = open(fr,'r')
	outp = join(outdir, basename(fr))
	outf = open(outp,'w')

	result = "["
	for line in ff.readlines():
		result += "\""+line.replace("\"","\\\"").replace("\n","")+"\","

	result = result[:-1] + "]"
	outf.write(result)
	outf.close()
	ff.close()


if __name__ == "__main__":
	main(sys.argv[1:])