## read normals from directory

function [N] = readNorm(dirName)
  load("-ascii", strcat(dirName,"/normX.octa"));  
  load("-ascii", strcat(dirName,"/normY.octa"));
  load("-ascii", strcat(dirName,"/normZ.octa"));

  wh = size(normX);

  N = zeros([3 wh]);
  N(1, :,:) = normX;
  N(2, :,:) = normY;
  N(3, :,:) = normZ;
end