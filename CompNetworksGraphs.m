%% Small script that generates the G1,G2,G3 graphs for the report of my 6th
%semester assignment on the Computer Networks I course @ECE AUTH.
%G1 is the graph where we see the system's response time for evere echo
%package sent at a minimum of 4 minutes(we also create an histogram for a
%better representation).
%G2 and its histogram are the respective graphs of the ARQ packages we
%received.
%Finally G3 is our estimation for the probability distribution of the
%amount of retransmissions we had.

%% Load text for G1 to import it into a column vector
filenameg1 = 'C:\Program Files\Polyspace\R2020a\bin\session1m\‪Time.txt';

% Format for each line of text:
formatSpec = '%f%[^\n\r]';

% Open the text file.
fileID = fopen(filenameg1,'r');

dataArray = textscan(fileID, formatSpec, 'Delimiter', '', 'WhiteSpace', '', 'TextType', 'string', 'EmptyValue', NaN,  'ReturnOnError', false);
fclose(fileID);
g1 = dataArray{:, 1};
clearvars filenameg1 formatSpec fileID dataArray ans;

%% Load text for G2 to import it into a column vector
filenameg2= 'C:\Program Files\Polyspace\R2020a\bin\session1m\Time_arq.txt';

% Format for each line of text:

formatSpec = '%f%[^\n\r]';

% Open the text file.
fileID = fopen(filenameg2,'r');
dataArray = textscan(fileID, formatSpec, 'Delimiter', '', 'WhiteSpace', '', 'TextType', 'string', 'EmptyValue', NaN,  'ReturnOnError', false);
fclose(fileID);
g2 = dataArray{:, 1};
clearvars filenameg2 formatSpec fileID dataArray ans;

%% Load G3 from eclipse console and copied clipboard generated from our java code.
g3 =importdata('-pastespecial');


%% G1
figure 
plot(g1)
xlabel('Number of packages')
ylabel('Response Time (ms)')
title('G1-ResponseTimes')
saveas(gcf,'G1.png');
%histogram with 35 nbins to better visualize the response time distribution
figure
histogram(g1,35)
xlabel('Response Time (ms)')
ylabel('Number of packages')
title('G1-Histogram')
saveas(gcf,'G1_Histogram.png');


%% G2
figure
plot(g2)
xlabel('Number of packages')
ylabel('Response Time of successful sending (ms)')
title('G2-ResponseTimes')
saveas(gcf,'G2.png');
%histogram with 35 nbins to better visualize the response time distribution
figure
histogram(g2,35)
xlabel('Response Time (ms)')
ylabel('Number of packages')
title('G2-Histogram')
saveas(gcf,'G2_Histogram.png');

%% G3
figure 
bar(g3)
xlabel('Retries')
ylabel('# of times for each retry')
title('G3')
saveas(gcf,'G3.png');
%line chart to show the expontetial distribution of the nack packages
figure 
plot(g3)
xlabel('retries')
ylabel('# of times for each retry')
title('G3 Line Chart')
saveas(gcf,'G3_linechart.png');
