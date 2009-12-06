## calculate square differences between given image and bootstrap set

function [D] = calcDiffs(workDir, Itra)
  fileNames = sort(glob(sprintf("%s/*.jpg", workDir)));
  K = length(fileNames);
  D = zeros(K, 1);
  for k=1:K
    imgK = imread(fileNames{k});
    diff = (Itra-imgK(:))/255.0;
    D(k) = dot(diff,diff);
  endfor
endfunction