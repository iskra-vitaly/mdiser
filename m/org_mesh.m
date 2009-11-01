function [R] = org_mesh(M, V, width, height)

minV = min(V);
maxV = max(V);

minX = minV(1);
maxX = maxV(1);

minY = minV(2);
maxY = maxV(2);

Rx = maxX - minX;
Ry = maxY - minY;

scaledXY = [(V(:, 1) - minX)/Rx*(width-1)+1 (V(:, 2) - minY)/Ry*(height-1)+1];

XX = [V(M(:, 1), 1) V(M(:, 2), 1) V(M(:, 3), 1)];
YY = [V(M(:, 1), 2) V(M(:, 2), 2) V(M(:, 3), 2)];

avgXX = mean(XX')';
avgYY = mean(YY')';

minAXX = min(avgXX');
minAYY = min(avgYY');
maxAXX = max(avgXX');
maxAYY = max(avgYY');

Rx = maxAXX - minAXX;
Ry = maxAYY - minAYY;

scaledAvgXY = [(avgXX-minX)/Rx*(width-1)+1 (avgYY-minY)/Ry*(height-1)+1];
idxXY = round(scaledAvgXY);

R = zeros(width, height);

deconvidx = (idxXY(:, 2)-1)*height+idxXY(:, 1);

nM = size(idxXY, 1);

R(deconvidx) = 1:nM;
