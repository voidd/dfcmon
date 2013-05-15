#!/usr/bin/perl

use WWW::Mechanize;
use HTTP::Response;
use strict;

  print "Content-type: text/html\n\n";

  my $username = "AASaltykov";
  my $password = "111";
  my $docbase = "MC";
  my $outfile = "out.txt";

  my $mech = WWW::Mechanize->new( );

  my $url = 'http://217.74.47.190:7003/sad/login.jsp';
  
  $mech->agent("Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB7.4; InfoPath.2; SV1; .NET CLR 3.3.69573; WOW64; en-US)");
  $mech->get($url);
  $mech->dump_headers;
  $mech->dump_forms;
  $mech->form_name('Login_0');
  $mech->field('Login_username_0' => "$username");
  $mech->field('Login_password_0' => "$password");
  $mech->field('Login_docbase_0' => "$docbase");
  $mech->submit_form(
    form_number => 1,
    fields => {'Login_username_0' => "$username",
    'Login_password_0' => "$password",
    'Login_docbase_0' => "$docbase" }
  );
  $mech->click('Login_loginButton_0');
  die unless ($mech->success);

  my $response = $mech->response();
  #if ($response->is_success) {
  #      print $response->decoded_content;
  #}
  $mech->response()->content(format => 'text');
  #$mech->dump_headers;
  $mech->dump_forms;

  open(OUTFILE, ">$outfile");
  print OUTFILE "$response";
  close(OUTFILE);