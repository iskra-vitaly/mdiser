function [DD] = benchDiffMulti(workDir, cnt)
  DD = cell(cnt, 1);
  for img=1:cnt
    subj = round(rand*30+1);
    cond = round(rand*400+1);
    fileName = sprintf("%s/%02d.%04d.jpg", workDir, subj, cond);
    IMG = imread(fileName);
    [tm, D] = benchIDiff(workDir, IMG(:));
    ds = sort(sqrt(D));
    sigma = ds(round(length(ds)/10));
    DD(img) = struct("subj", subj, "cond", cond, "file", fileName, "sigma", sigma);
  endfor
  save("benchDiffMulti.res", "DD");
endfunction